package org.ripple.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.ripple.power.RippleSchemas.BinaryFormatField;
import org.ripple.power.RippleSchemas.PrimitiveTypes;
import org.ripple.power.RippleSchemas.TransactionTypes;

public class RippleObject {

	HashMap<BinaryFormatField, Object> fields = new HashMap<BinaryFormatField, Object>();

	private static RippleSerializer binSer = new RippleSerializer();

	public RippleObject() {
	}

	public RippleObject(RippleObject serObjToSign) {
		this.fields.putAll(serObjToSign.fields);
	}

	public RippleObject getUnsignedCopy() {
		RippleObject copy = new RippleObject(this);
		copy.removeField(BinaryFormatField.TxnSignature);
		return copy;
	}

	public byte[] generateHashFromBinaryObject() {
		byte[] bytesToSign = binSer.writeBinaryObject(this).array();
		byte[] prefixedBytesToHash = new byte[bytesToSign.length + 4];
		prefixedBytesToHash[0] = (byte) 'S';
		prefixedBytesToHash[1] = (byte) 'T';
		prefixedBytesToHash[2] = (byte) 'X';
		prefixedBytesToHash[3] = (byte) 0;
		System.arraycopy(bytesToSign, 0, prefixedBytesToHash, 4, bytesToSign.length);
		byte[] hashOfBytes = Helper.halfSHA512(prefixedBytesToHash);
		return hashOfBytes;
	}

	public byte[] getTransactionHash() {
		byte[] signedBytes = binSer.writeBinaryObject(this).array();
		byte[] prefixedSignedBytes = new byte[signedBytes.length + 4];
		prefixedSignedBytes[0] = (byte) 'T';
		prefixedSignedBytes[1] = (byte) 'X';
		prefixedSignedBytes[2] = (byte) 'N';
		prefixedSignedBytes[3] = (byte) 0;
		System.arraycopy(signedBytes, 0, prefixedSignedBytes, 4, signedBytes.length);

		byte[] hashOfTransaction = Helper.halfSHA512(prefixedSignedBytes);
		return hashOfTransaction;
	}

	public Object getField(BinaryFormatField transactiontype) {
		Object obj = fields.get(transactiontype);
		if (obj == null) {
			return null;
		}
		return obj;
	}

	public void putField(BinaryFormatField field, Object value) {
		fields.put(field, value);
	}

	public TransactionTypes getTransactionType() {
		Object txTypeObj = getField(BinaryFormatField.TransactionType);
		if (txTypeObj == null) {
			throw new NullPointerException("No transaction type field found");
		}
		return TransactionTypes.fromType((int) txTypeObj);
	}

	public String toJSONString() {
		JSONObject root = new JSONObject();
		for (Entry<BinaryFormatField, Object> field : fields.entrySet()) {
			PrimitiveTypes primitive = field.getKey().primitive;
			if (primitive == PrimitiveTypes.UINT8 || primitive == PrimitiveTypes.UINT16
					|| primitive == PrimitiveTypes.UINT32 || primitive == PrimitiveTypes.UINT64) {
				root.put(field.getKey().toString(), field.getValue());
			} else {
				root.put(field.getKey().toString(), field.getValue().toString());
			}
		}
		return root.toString();
	}

	public List<BinaryFormatField> getSortedField() {
		ArrayList<BinaryFormatField> sortedFields = new ArrayList<BinaryFormatField>(fields.keySet());
		Collections.sort(sortedFields);
		return sortedFields;
	}

	public Object removeField(BinaryFormatField fieldToBeRemoved) {
		return fields.remove(fieldToBeRemoved);
	}

	@Override
	public String toString() {
		return "RippleBinaryObject [fields=" + fields + "]";
	}
}
