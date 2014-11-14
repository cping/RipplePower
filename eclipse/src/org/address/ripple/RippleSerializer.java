package org.address.ripple;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import org.address.ripple.RippleSchemas.BinaryFormatField;
import org.address.ripple.RippleSchemas.PrimitiveTypes;
import org.address.utils.CoinUtils;
import org.ripple.power.txns.IssuedCurrency;

import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.STArray;
import com.ripple.core.coretypes.STObject;

public class RippleSerializer {

	protected static final long MIN_VALUE = 1000000000000000l;
	protected static final long MAX_VALUE = 9999999999999999l;

	public RippleObject readBinaryObject(ByteBuffer input) {
		RippleObject serializedObject = new RippleObject();
		while (input.hasRemaining()) {
			byte firstByte = input.get();
			int type = (0xF0 & firstByte) >> 4;
			if (type == 0) {
				type = input.get();
			}
			int field = 0x0F & firstByte;
			if (field == 0) {
				field = input.get();
				firstByte = (byte) field;
			}

			BinaryFormatField serializedField = BinaryFormatField.lookup(type,
					field);
			Object value = readPrimitive(input, serializedField.primitive);
			serializedObject.fields.put(serializedField, value);
		}
		return serializedObject;
	}

	protected Object readPrimitive(ByteBuffer input, PrimitiveTypes primitive) {
		if (primitive == PrimitiveTypes.UINT16) {
			return 0xFFFFFFFF & input.getShort();
		} else if (primitive == PrimitiveTypes.UINT32) {
			return 0xFFFFFFFFFFFFFFFFl & input.getInt();
		} else if (primitive == PrimitiveTypes.UINT64) {
			byte[] eightBytes = new byte[8];
			input.get(eightBytes);
			return new BigInteger(1, eightBytes);
		} else if (primitive == PrimitiveTypes.HASH128) {
			byte[] sixteenBytes = new byte[16];
			input.get(sixteenBytes);
			return sixteenBytes;
		} else if (primitive == PrimitiveTypes.HASH256) {
			byte[] thirtyTwoBytes = new byte[32];
			input.get(thirtyTwoBytes);
			return thirtyTwoBytes;
		} else if (primitive == PrimitiveTypes.AMOUNT) {
			return readAmount(input);
		} else if (primitive == PrimitiveTypes.VARIABLE_LENGTH) {
			return readVariableLength(input);
		} else if (primitive == PrimitiveTypes.ACCOUNT) {
			return readAccount(input);
		} else if (primitive == PrimitiveTypes.OBJECT) {
			throw new RuntimeException("Object type, not yet supported");
		} else if (primitive == PrimitiveTypes.ARRAY) {
			throw new RuntimeException("Array type, not yet supported");
		} else if (primitive == PrimitiveTypes.UINT8) {
			return 0xFFFF & input.get();
		} else if (primitive == PrimitiveTypes.HASH160) {
			return readIssuer(input);
		} else if (primitive == PrimitiveTypes.PATHSET) {
			return readPathSet(input);
		} else if (primitive == PrimitiveTypes.VECTOR256) {
			throw new RuntimeException("Vector");
		}
		throw new RuntimeException("Unsupported primitive " + primitive);
	}

	protected RippleAddress readAccount(ByteBuffer input) {
		byte[] accountBytes = readVariableLength(input);
		return new RippleAddress(accountBytes);
	}

	protected IssuedCurrency readAmount(ByteBuffer input) {
		long offsetNativeSignMagnitudeBytes = input.getLong();
		boolean isXRPAmount = (0x8000000000000000l & offsetNativeSignMagnitudeBytes) == 0;
		int sign = (0x4000000000000000l & offsetNativeSignMagnitudeBytes) == 0 ? -1
				: 1;
		int offset = (int) ((offsetNativeSignMagnitudeBytes & 0x3FC0000000000000l) >>> 54);
		long longMagnitude = offsetNativeSignMagnitudeBytes & 0x3FFFFFFFFFFFFFl;
		if (isXRPAmount) {
			BigDecimal magnitude = BigDecimal.valueOf(sign * longMagnitude);
			return new IssuedCurrency(magnitude);
		} else {
			String currencyStr = readCurrency(input);
			RippleAddress issuer = readIssuer(input);
			if (offset == 0 || longMagnitude == 0) {
				return new IssuedCurrency(BigDecimal.ZERO, issuer, currencyStr);
			}

			int decimalPosition = 97 - offset;
			if (decimalPosition < IssuedCurrency.MIN_SCALE
					|| decimalPosition > IssuedCurrency.MAX_SCALE) {
				throw new RuntimeException("invalid scale " + decimalPosition);
			}
			BigInteger biMagnitude = BigInteger.valueOf(sign * longMagnitude);
			BigDecimal fractionalValue = new BigDecimal(biMagnitude,
					decimalPosition);
			return new IssuedCurrency(fractionalValue, issuer, currencyStr);
		}
	}

	protected RippleAddress readIssuer(ByteBuffer input) {
		byte[] issuerBytes = new byte[20];
		input.get(issuerBytes);
		return new RippleAddress(issuerBytes);
	}

	protected String readCurrency(ByteBuffer input) {
		byte[] unknown = new byte[12];
		input.get(unknown);
		byte[] currency = new byte[8];
		input.get(currency);
		return new String(currency, 0, 3);
	}

	protected byte[] readVariableLength(ByteBuffer input) {
		int byteLen = 0;
		int firstByte = input.get();
		int secondByte = 0;
		if (firstByte < 192) {
			byteLen = firstByte;
		} else if (firstByte < 240) {
			secondByte = input.get();
			byteLen = 193 + (firstByte - 193) * 256 + secondByte;
		} else if (firstByte < 254) {
			secondByte = input.get();
			int thirdByte = input.get();
			byteLen = 12481 + (firstByte - 241) * 65536 + secondByte * 256
					+ thirdByte;
		} else {
			throw new RuntimeException("firstByte=" + firstByte
					+ ", value reserved");
		}

		byte[] variableBytes = new byte[byteLen];
		input.get(variableBytes);
		return variableBytes;
	}

	protected RipplePathSet readPathSet(ByteBuffer input) {
		RipplePathSet pathSet = new RipplePathSet();
		RipplePath path = null;
		while (true) {
			byte pathElementType = input.get();
			if (pathElementType == (byte) 0x00) {
				break;
			}
			if (path == null) {
				path = new RipplePath();
				pathSet.add(path);
			}
			if (pathElementType == (byte) 0xFF) {
				path = null;
				continue;
			}

			RipplePathElement pathElement = new RipplePathElement();
			path.add(pathElement);
			if ((pathElementType & 0x01) != 0) {
				pathElement.account = readIssuer(input);
			}
			if ((pathElementType & 0x10) != 0) {
				pathElement.currency = readCurrency(input);
			}
			if ((pathElementType & 0x20) != 0) {
				pathElement.issuer = readIssuer(input);
			}
		}

		return pathSet;
	}

	public ByteBuffer writeBinaryObject(RippleObject serializedObj) {
		ByteBuffer output = ByteBuffer.allocate(2000);
		List<BinaryFormatField> sortedFields = serializedObj.getSortedField();
		for (BinaryFormatField field : sortedFields) {
			byte typeHalfByte = 0;
			if (field.primitive.typeCode <= 15) {
				typeHalfByte = (byte) (field.primitive.typeCode << 4);
			}
			byte fieldHalfByte = 0;
			if (field.fieldId <= 15) {
				fieldHalfByte = (byte) (field.fieldId & 0x0F);
			}
			output.put((byte) (typeHalfByte | fieldHalfByte));
			if (typeHalfByte == 0) {
				output.put((byte) field.primitive.typeCode);
			}
			if (fieldHalfByte == 0) {
				output.put((byte) field.fieldId);
			}

			writePrimitive(output, field.primitive,
					serializedObj.getField(field));
		}
		output.flip();
		ByteBuffer compactBuffer = ByteBuffer.allocate(output.limit());
		compactBuffer.put(output);
		compactBuffer.flip();
		return compactBuffer;
	}

	protected void writePrimitive(ByteBuffer output, PrimitiveTypes primitive,
			Object value) {
		if (primitive == PrimitiveTypes.UINT16) {
			int intValue = (int) value;
			if (intValue > 0xFFFF) {
				throw new RuntimeException("UINT16 overflow for value " + value);
			}
			output.put((byte) (intValue >> 8 & 0xFF));
			output.put((byte) (intValue & 0xFF));
		} else if (primitive == PrimitiveTypes.UINT32) {
			long longValue = (long) value;
			if (longValue > 0xFFFFFFFFl) {
				throw new RuntimeException("UINT32 overflow for value " + value);
			}
			output.put((byte) (longValue >> 24 & 0xFF));
			output.put((byte) (longValue >> 16 & 0xFF));
			output.put((byte) (longValue >> 8 & 0xFF));
			output.put((byte) (longValue & 0xFF));
		} else if (primitive == PrimitiveTypes.UINT64) {
			byte[] biBytes = RipplePrivateKey.bigIntegerToBytes(
					(BigInteger) value, 8);
			if (biBytes.length != 8) {
				throw new RuntimeException("UINT64 overflow for value " + value);
			}
			output.put(biBytes);
		} else if (primitive == PrimitiveTypes.HASH128) {
			byte[] sixteenBytes = (byte[]) value;
			if (sixteenBytes.length != 16) {
				throw new RuntimeException("value " + value
						+ " is not a HASH128");
			}
			output.put(sixteenBytes);
		} else if (primitive == PrimitiveTypes.HASH256) {
			byte[] thirtyTwoBytes = (byte[]) value;
			if (thirtyTwoBytes.length != 32) {
				throw new RuntimeException("value " + value
						+ " is not a HASH256");
			}
			output.put(thirtyTwoBytes);
		} else if (primitive == PrimitiveTypes.AMOUNT) {
			if (value instanceof String) {
				writeAmount(output, new IssuedCurrency((String) value));
			} else {
				writeAmount(output, (IssuedCurrency) value);
			}
		} else if (primitive == PrimitiveTypes.VARIABLE_LENGTH) {
			if (value instanceof Byte[]) {
				writeVariableLength(output, (byte[]) value);
			} else if (value instanceof String) {
				writeVariableLength(output, CoinUtils.fromHex((String) value));
			}else {
				throw new RuntimeException("Variable type, not yet supported");
			}
		} else if (primitive == PrimitiveTypes.ACCOUNT) {
			if (value instanceof String) {
				writeAccount(output, new RippleAddress((String) value));
			} else {
				writeAccount(output, (RippleAddress) value);
			}
		} else if (primitive == PrimitiveTypes.OBJECT) {
			if (value instanceof STObject) {
				output.put(((STObject) value).toBytes());
			} else {
				throw new RuntimeException("Object type, not yet supported");
			}
		} else if (primitive == PrimitiveTypes.ARRAY) {
			if (value instanceof STArray) {
				output.put(((STArray) value).toBytes());
			} else {
				throw new RuntimeException("Array type, not yet supported");
			}
		} else if (primitive == PrimitiveTypes.UINT8) {
			int intValue = (int) value;
			if (intValue > 0xFF) {
				throw new RuntimeException("UINT8 overflow for value " + value);
			}
			output.put((byte) value);
		} else if (primitive == PrimitiveTypes.HASH160) {
			writeIssuer(output, (RippleAddress) value);
		} else if (primitive == PrimitiveTypes.PATHSET) {
			writePathSet(output, (RipplePathSet) value);
		} else if (primitive == PrimitiveTypes.VECTOR256) {
			throw new RuntimeException("Vector");
		} else {
			throw new RuntimeException("Unsupported primitive " + primitive);
		}
	}

	protected void writePathSet(ByteBuffer output, RipplePathSet pathSet) {
		loopPathSet: for (int i = 0; i < pathSet.size(); i++) {
			RipplePath path = pathSet.get(i);
			for (int j = 0; j < path.size(); j++) {
				RipplePathElement pathElement = path.get(j);
				byte pathElementType = 0;
				if (pathElement.account != null) {
					pathElementType |= 0x01;
				}
				if (pathElement.currency != null) {
					pathElementType |= 0x10;
				}
				if (pathElement.issuer != null) {
					pathElementType |= 0x20;
				}
				output.put(pathElementType);

				if (pathElement.account != null) {
					writeIssuer(output, pathElement.account);
				}
				if (pathElement.currency != null) {
					writeCurrency(output, pathElement.currency);
				}
				if (pathElement.issuer != null) {
					writeIssuer(output, pathElement.issuer);
				}
				if (i + 1 == pathSet.size() && j + 1 == path.size()) {
					break loopPathSet;
				}
			}

			output.put((byte) 0xFF);
		}
		output.put((byte) 0);
	}

	protected void writeIssuer(ByteBuffer output, RippleAddress value) {
		byte[] issuerBytes = value.getBytes();
		output.put(issuerBytes);
	}

	protected void writeAccount(ByteBuffer output, RippleAddress address) {
		writeVariableLength(output, address.getBytes());
	}

	protected void writeVariableLength(ByteBuffer output, byte[] value) {
		if (value.length < 192) {
			output.put((byte) value.length);
		} else if (value.length < 12480) {
			int firstByte = (value.length / 256) + 193;
			output.put((byte) firstByte);
			int secondByte = value.length - firstByte - 193;
			output.put((byte) secondByte);
		} else if (value.length < 918744) {
			int firstByte = (value.length / 65536) + 241;
			output.put((byte) firstByte);
			int secondByte = (value.length - firstByte) / 256;
			output.put((byte) secondByte);
			int thirdByte = value.length - firstByte - secondByte - 12481;
			output.put((byte) thirdByte);
		}
		output.put(value);
	}

	protected void writeAmount(ByteBuffer output,
			IssuedCurrency denominatedCurrency) {
		long offsetNativeSignMagnitudeBytes = 0;
		if (denominatedCurrency.amount.signum() > 0) {
			offsetNativeSignMagnitudeBytes |= 0x4000000000000000l;
		}
		if (denominatedCurrency.currency == null) {
			long drops = denominatedCurrency.amount.longValue();
			offsetNativeSignMagnitudeBytes |= drops;
			output.putLong(offsetNativeSignMagnitudeBytes);
		} else {
			Amount amount = Amount
					.fromIOUString(denominatedCurrency.toString());
			output.put(amount.toBytes());
		}
	}

	protected void writeCurrency(ByteBuffer output, String currency) {
		byte[] currencyBytes = new byte[20];
		System.arraycopy(currency.getBytes(), 0, currencyBytes, 12, 3);
		output.put(currencyBytes);
	}

}
