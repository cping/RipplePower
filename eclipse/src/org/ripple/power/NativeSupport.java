package org.ripple.power;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.zip.CRC32;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.MathUtils;

import com.ripple.config.Config;
import com.ripple.crypto.ecdsa.SECP256K1;
import com.ripple.utils.Utils;

public class NativeSupport {

	public static boolean isWindows = System.getProperty("os.name").contains(
			"Windows");
	public static boolean isLinux = System.getProperty("os.name").contains(
			"Linux");
	public static boolean isMac = System.getProperty("os.name").contains("Mac");
	public static boolean isAndroid = false;
	public static boolean is64Bit = System.getProperty("os.arch").equals(
			"amd64");

	private static HashSet<String> loadedLibraries = new HashSet<String>();

	public static String CRC(InputStream input) {
		if (input == null) {
			return "" + System.nanoTime();
		}
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096];
		try {
			while (true) {
				int length = input.read(buffer);
				if (length == -1)
					break;
				crc.update(buffer, 0, length);
			}
		} catch (Exception ex) {
			try {
				input.close();
			} catch (Exception ignored) {
			}
		}
		return Long.toString(crc.getValue());
	}

	public static String libNames(String libraryName) {
		if (isWindows) {
			return libraryName + (is64Bit ? "64.dll" : ".dll");
		}
		if (isLinux) {
			return "lib" + libraryName + (is64Bit ? "64.so" : ".so");
		}
		if (isMac) {
			return "lib" + libraryName + ".dylib";
		}
		return libraryName;
	}

	public static synchronized void loadJNI(String libraryName)
			throws Throwable {
		libraryName = libNames(libraryName);
		if (loadedLibraries.contains(libraryName)) {
			return;
		}
		try {
			if (isAndroid) {
				System.loadLibrary(libraryName);
			} else {
				System.load(export(libraryName, null).getAbsolutePath());
			}
		} catch (Throwable ex) {
			throw new Exception(ex);
		}
		loadedLibraries.add(libraryName);
	}

	public static File export(String sourcePath, String dirName)
			throws IOException {
		return export(sourcePath, dirName, null);
	}

	public static File export(String sourcePath, String dirName, String name)
			throws IOException {
		ClassLoader loader = NativeSupport.class.getClassLoader();
		String sourceCrc = CRC(loader.getResourceAsStream(sourcePath));
		if (dirName == null) {
			dirName = sourceCrc;
		}
		File extractedDir = new File(System.getProperty("java.io.tmpdir")
				+ "/loon" + LSystem.getUserName() + "/" + dirName);
		File extractedFile = new File(extractedDir, name == null ? new File(
				sourcePath).getName() : name);
		String extractedCrc = null;
		if (extractedFile.exists()) {
			try {
				extractedCrc = CRC(new FileInputStream(extractedFile));
			} catch (FileNotFoundException ignored) {
			}
		}
		if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
			try {
				InputStream input = loader.getResourceAsStream(sourcePath);
				if (input == null) {
					return null;
				}
				extractedDir.mkdirs();
				FileOutputStream output = new FileOutputStream(extractedFile);
				byte[] buffer = new byte[4096];
				while (true) {
					int length = input.read(buffer);
					if (length == -1)
						break;
					output.write(buffer, 0, length);
				}
				input.close();
				output.close();
			} catch (IOException ex) {
				throw new RuntimeException("Error extracting file: "
						+ sourcePath, ex);
			}
		}
		return extractedFile.exists() ? extractedFile : null;
	}

	private static boolean nativesLoaded;

	public static final int SIZEOF_BYTE = 1;

	public static final int SIZEOF_SHORT = 2;

	public static final int SIZEOF_FLOAT = 4;

	public static final int SIZEOF_INT = SIZEOF_FLOAT;

	public static final int SIZEOF_DOUBLE = 8;

	public static final int SIZEOF_LONG = SIZEOF_DOUBLE;

	private static boolean useLoonNative;

	static {
		String vm = System.getProperty("java.vm.name");
		if (vm != null && vm.contains("Dalvik")) {
			isAndroid = true;
			isWindows = false;
			isLinux = false;
			isMac = false;
			is64Bit = false;
		}
		try {
			loadJNI("jcoin");
			useLoonNative = true;
			System.out.println("Support of the native method call");
		} catch (Throwable e) {
			useLoonNative = false;
		}
	}

	public static boolean UseLoonNative() {
		return useLoonNative;
	}

	public static void CloseLoonNative() {
		useLoonNative = false;
	}

	private static native String getByteKeys(byte[] bytes, boolean compressed);

	private static native String getHashKeys(byte[] bytes, boolean compressed);

	private static native byte[] getNxtHashKeys(byte[] bytes);

	private static native boolean findByteAddress(byte[] dst, byte[] src);

	private static native String getRippleBase58(byte[] bytes);

	private static native byte[] getRipemd160Sha256(byte[] dst);

	private static native String getSecp256k1ToPublic(byte[] dst);

	private static native byte[] getSha512Quarter(byte[] dst);

	private static native byte[] getSha512Half(byte[] dst);

	public static byte[] toRipemd160Sha256(byte[] dst) {
		if (useLoonNative) {
			try {
				return getRipemd160Sha256(dst);
			} catch (Throwable ex) {
				return CoinUtils.sha256ripemd160(dst);
			}
		} else {
			return CoinUtils.sha256ripemd160(dst);
		}
	}

	public static byte[] getNxtKey(String secret) {
		byte[] publicKeyHash = null;
		try {
			publicKeyHash = secret.getBytes(LSystem.encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		if (useLoonNative) {
			return getNxtHashKeys(publicKeyHash);
		} else {
			byte[] publicKey = new byte[32];
			Curve25519.keygen(publicKey, Helper.update(publicKeyHash));
			publicKeyHash = Helper.update(publicKey);
		}
		return publicKeyHash;
	}

	public static String getBitcoinPrivateKey(String secret) {
		return getBitcoinPrivateKey(secret, false);
	}

	public static String getBitcoinPrivateKey(String secret, boolean compressed) {
		if (useLoonNative) {
			try {
				return getByteKeys(secret.getBytes(LSystem.encoding),
						compressed);
			} catch (Exception e) {
				return "";
			}
		} else {
			byte[] hash = null;
			try {
				hash = Helper.update(secret.getBytes(LSystem.encoding));
			} catch (UnsupportedEncodingException e) {
				return "";
			}
			byte[] pub = CoinUtils.generatePublicKey(new BigInteger(1, hash),
					compressed);
			boolean c = (pub.length == 33);
			byte[] bytes = hash;
			if (c) {
				bytes = new byte[33];
				System.arraycopy(hash, 0, bytes, 0, 32);
				bytes[32] = 1;
			}
			int size = bytes.length;
			byte[] addressBytes = new byte[1 + size + 4];
			// type 0 is 128
			addressBytes[0] = ((byte) 128);
			System.arraycopy(bytes, 0, addressBytes, 1, size);
			byte[] check = Helper.doubleDigest(addressBytes, 0, size + 1);
			System.arraycopy(check, 0, addressBytes, size + 1, 4);
			return CoinUtils.publicKeyToAddress(pub) + ","
					+ CoinUtils.encodeBase58(addressBytes);
		}
	}

	public static String getBitcoinBigIntegerPrivateKey(BigInteger id,
			boolean compressed) {
		String hashString = MathUtils.addZeros(
				CoinUtils.toHex(id.toByteArray()), 64);
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return getHashKeys(hash, compressed);
			} catch (Exception e) {
				return "";
			}
		} else {
			byte[] pub = CoinUtils.generatePublicKey(new BigInteger(hash),
					compressed);
			boolean c = (pub.length == 33);
			byte[] bytes = hash;
			if (c) {
				bytes = new byte[33];
				System.arraycopy(hash, 0, bytes, 0, 32);
				bytes[32] = 1;
			}
			int size = bytes.length;
			byte[] addressBytes = new byte[1 + size + 4];
			// type 0 is 128
			addressBytes[0] = ((byte) 128);
			System.arraycopy(bytes, 0, addressBytes, 1, size);
			byte[] check = Helper.doubleDigest(addressBytes, 0, size + 1);
			System.arraycopy(check, 0, addressBytes, size + 1, 4);
			return CoinUtils.publicKeyToAddress(pub) + ","
					+ CoinUtils.encodeBase58(addressBytes);
		}
	}

	public static String getBitcoinBigIntegerPrivateKey(String hashString,
			boolean compressed) {
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return getHashKeys(hash, compressed);
			} catch (Exception e) {
				return "";
			}
		} else {
			byte[] pub = CoinUtils.generatePublicKey(new BigInteger(hash),
					compressed);
			boolean c = (pub.length == 33);
			byte[] bytes = hash;
			if (c) {
				bytes = new byte[33];
				System.arraycopy(hash, 0, bytes, 0, 32);
				bytes[32] = 1;
			}
			int size = bytes.length;
			byte[] addressBytes = new byte[1 + size + 4];
			// type 0 is 128
			addressBytes[0] = ((byte) 128);
			System.arraycopy(bytes, 0, addressBytes, 1, size);
			byte[] check = Helper.doubleDigest(addressBytes, 0, size + 1);
			System.arraycopy(check, 0, addressBytes, size + 1, 4);
			return CoinUtils.publicKeyToAddress(pub) + ","
					+ CoinUtils.encodeBase58(addressBytes);
		}
	}

	private static native String getRippleByteKeys(byte[] seedBytes);

	private static native byte[] getRippleHashKeys(byte[] seedBytes);

	private static native String[] getRippleBatchKeys(byte[] res, int max);

	public static byte[] createKeyPair(byte[] seedBytes) {
		BigInteger secret, pub, privateGen, order = SECP256K1.order();
		byte[] privateGenBytes;
		byte[] publicGenBytes;
		int i = 0, seq = 0;
		for (;;) {
			privateGenBytes = hashedIncrement(seedBytes, i++);
			privateGen = Utils.uBigInt(privateGenBytes);
			if (privateGen.compareTo(order) == -1) {
				break;
			}
		}
		publicGenBytes = CoinUtils.generateKey(privateGen);
		i = 0;
		for (;;) {
			byte[] secretBytes = hashedIncrement(
					appendIntBytes(publicGenBytes, seq), i++);

			secret = Utils.uBigInt(secretBytes);
			if (secret.compareTo(order) == -1) {
				break;
			}
		}
		secret = secret.add(privateGen).mod(order);
		pub = Utils.uBigInt(CoinUtils.generateKey(secret));
		return pub.toByteArray();
	}

	private static byte[] hashedIncrement(byte[] bytes, int increment) {
		if (useLoonNative) {
			try {
				return getSha512Half(appendIntBytes(bytes, increment));
			} catch (Throwable t) {
				return Helper.halfSHA512(appendIntBytes(bytes, increment));
			}
		} else {
			return Helper.halfSHA512(appendIntBytes(bytes, increment));
		}
	}

	private static byte[] appendIntBytes(byte[] in, int i) {
		byte[] out = new byte[in.length + 4];

		System.arraycopy(in, 0, out, 0, in.length);

		out[in.length] = (byte) ((i >>> 24) & 0xFF);
		out[in.length + 1] = (byte) ((i >>> 16) & 0xFF);
		out[in.length + 2] = (byte) ((i >>> 8) & 0xFF);
		out[in.length + 3] = (byte) ((i) & 0xFF);

		return out;
	}

	public static String getRipplePrivateKey(String secret) {
		if (useLoonNative) {
			try {
				return getRippleByteKeys(secret.getBytes(LSystem.encoding));
			} catch (Throwable t) {
				try {
					byte[] master = Helper.quarterSha512(secret
							.getBytes(LSystem.encoding));
					String seed = Config.getB58IdentiferCodecs()
							.encodeFamilySeed(master);
					return new RipplePublicKey(createKeyPair(master))
							.getAddress().toString() + "," + seed;
				} catch (Exception ex) {
					return "";
				}
			}
		} else {
			try {
				byte[] master = Helper.quarterSha512(secret
						.getBytes(LSystem.encoding));
				String seed = Config.getB58IdentiferCodecs().encodeFamilySeed(
						master);
				return new RipplePublicKey(createKeyPair(master)).getAddress()
						.toString() + "," + seed;
			} catch (Exception e) {
				return "";
			}
		}
	}

	public static String getRippleBigIntegerPrivateKey(BigInteger id) {
		String hashString = MathUtils.addZeros(
				CoinUtils.toHex(id.toByteArray()), 32);
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return new String(getRippleHashKeys(hash));
			} catch (Throwable t) {
				try {
					String seed = Config.getB58IdentiferCodecs()
							.encodeFamilySeed(hash);
					return new RipplePublicKey(createKeyPair(hash))
							.getAddress().toString() + "," + seed;
				} catch (Exception e) {
					return "";
				}
			}
		} else {
			try {
				String seed = Config.getB58IdentiferCodecs().encodeFamilySeed(
						hash);
				return new RipplePublicKey(createKeyPair(hash)).getAddress()
						.toString() + "," + seed;
			} catch (Exception e) {
				return "";
			}
		}
	}

	public static String getRippleBigIntegerPrivateKey(String hashString) {
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return new String(getRippleHashKeys(hash));
			} catch (Throwable t) {
				try {
					String seed = Config.getB58IdentiferCodecs()
							.encodeFamilySeed(hash);
					return new RipplePublicKey(createKeyPair(hash))
							.getAddress().toString() + "," + seed;
				} catch (Exception e) {
					return "";
				}
			}
		} else {
			try {
				String seed = Config.getB58IdentiferCodecs().encodeFamilySeed(
						hash);
				return new RipplePublicKey(createKeyPair(hash)).getAddress()
						.toString() + "," + seed;
			} catch (Exception e) {
				return "";
			}
		}
	}

	public static String[] getRippleBatch(BigInteger id, int max) {
		String hashString = MathUtils.addZeros(
				CoinUtils.toHex(id.toByteArray()), 32);
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return getRippleBatchKeys(hash, max);
			} catch (Throwable t) {
				try {
					String[] lists = new String[max];
					BigInteger big = new BigInteger(hash);
					for (int i = 0; i < max; i++) {
						lists[i] = getRippleBigIntegerPrivateKey(big);
						big = big.add(BigInteger.ONE);
					}
					return lists;
				} catch (Exception e) {
					return null;
				}
			}
		} else {
			try {
				String[] lists = new String[max];
				BigInteger big = new BigInteger(hash);
				for (int i = 0; i < max; i++) {
					lists[i] = getRippleBigIntegerPrivateKey(big);
					big = big.add(BigInteger.ONE);
				}
				return lists;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static String getRippleBigIntegerPrivateKeys(BigInteger id) {
		String hashString = MathUtils.addZeros(
				CoinUtils.toHex(id.toByteArray()), 32);
		byte[] hash = CoinUtils.fromHex(hashString);
		if (useLoonNative) {
			try {
				return new String(getRippleHashKeys(hash));
			} catch (Throwable t) {
				try {
					String seed = Config.getB58IdentiferCodecs()
							.encodeFamilySeed(hash);
					return new RipplePublicKey(createKeyPair(hash))
							.getAddress().toString() + "," + seed;
				} catch (Exception e) {
					return "";
				}
			}
		} else {
			try {
				String seed = Config.getB58IdentiferCodecs().encodeFamilySeed(
						hash);
				return new RipplePublicKey(createKeyPair(hash)).getAddress()
						.toString() + "," + seed;
			} catch (Exception e) {
				return "";
			}
		}
	}

	private final static byte _tag = '\n';

	public final static boolean findCoinAddress(byte[] dst, byte[] src) {
		if (useLoonNative) {
			return findByteAddress(dst, src);
		} else {
			int patternOffset = 0;
			int len = dst.length;
			int index = 0;
			byte b = src[index++];
			int size = src.length;
			int mark = 0;
			for (; index < size;) {
				if (dst[patternOffset] == b) {
					patternOffset++;
					if (patternOffset == len) {
						return true;
					}
				} else {
					if (b == _tag) {
						mark = index;
					} else {
						int skip = index - mark;
						int go = 34 - skip;
						if (go + index < size && src[go + index] == _tag) {
							index = index + go;
						} else if ((go + index - 1) < size
								&& src[(go + index - 1)] == _tag) {
							index = (index + go - 1);
						}
					}
					patternOffset = 0;
				}
				b = src[index++];
			}
			return false;

		}
	}

}
