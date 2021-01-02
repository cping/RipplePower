package org.ripple.power.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import org.ripple.power.NativeSupport;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class AddressDataBase {

	private HashMap<String, FileOutputStream> pPutAddress = new HashMap<String, FileOutputStream>(1000);

	private int flag = 0;

	private boolean isOnlyLocked = false, isMode = false;

	protected final static String pIndexName = "index";

	private int pMaxCache = 700;

	public String pDirPath = "";

	public AddressDataBase(String dir) {
		this.pDirPath = dir;
	}

	public void setMode(boolean m) {
		this.isMode = m;
	}

	public int getMaxCache() {
		return pMaxCache;
	}

	public void setMaxCache(int max) {
		this.pMaxCache = max;
	}

	public String getDirPath() {
		return pDirPath;
	}

	public void setDirPath(String path) {
		this.pDirPath = path;
	}

	public boolean isOnlyLocked() {
		return isOnlyLocked;
	}

	public void setOnlyLocked(boolean locked) {
		this.isOnlyLocked = locked;
	}

	public final String toIndexAddress(String index_dir) {
		char[] chars = index_dir.toCharArray();
		StringBuilder sbr = new StringBuilder(pDirPath);
		sbr.append(LSystem.FS);
		int charSize = chars.length;
		boolean ex = charSize >= 40;
		if (ex) {
			if ((chars[2] >= '0' && chars[2] <= '9')) {
				sbr.append(chars[2]);
			} else {
				sbr.append(getNumber(chars[2]));
			}
			if ((chars[3] >= '0' && chars[3] <= '9')) {
				sbr.append(chars[3]);
			} else {
				sbr.append(getNumber(chars[3]));
			}
			sbr.append(LSystem.FS);
			switch (flag) {
			case 0:
				sbr.append(chars[4]);
				sbr.append(LSystem.FS);
				break;
			default:
				break;
			}
			if (isMode) {
				if (charSize > 40) {
					sbr.append(getNumber(chars[5]));
					sbr.append(LSystem.FS);
				}
				sbr.append(getNumber(chars[6]));
				sbr.append(LSystem.FS);
				sbr.append(getNumber(chars[7]) + pIndexName);
			} else {
				sbr.append(pIndexName);
			}
		} else {
			if (chars[0] == 'r' && charSize > 30) {
				sbr.append(chars[0]);
				sbr.append(chars[1]);
			} else {
				// sbr.append(chars[1]);
				if ((chars[0] >= '0' && chars[0] <= '9')) {
					sbr.append(chars[0]);
				} else {
					sbr.append(getNumber(chars[0]));
				}
				if ((chars[1] >= '0' && chars[1] <= '9')) {
					sbr.append(chars[1]);
				} else {
					sbr.append(getNumber(chars[1]));
				}
			}
			sbr.append(LSystem.FS);
			switch (flag) {
			case 0:
				sbr.append(chars[2]);
				sbr.append(LSystem.FS);
				break;
			default:
				break;
			}
			if (isMode) {
				if (charSize > 30) {
					sbr.append(getNumber(chars[3]));
					sbr.append(LSystem.FS);
				}
				sbr.append(getNumber(chars[4]));
				sbr.append(LSystem.FS);
				sbr.append(getNumber(chars[5]) + pIndexName);
			} else {
				sbr.append(pIndexName);
			}
		}

		return sbr.toString();
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int f) {
		this.flag = f;
	}

	private final static int getNumber(char letter) {
		//a
		if ((letter >= '0' && letter <= '4')) {
			return 0;
		} else if ((letter >= '5' && letter <= '9')) {
			return 1;
		} else if (letter == 'a' || letter == 'b' || letter == 'c' || letter == 'd' || letter == 'e') {
			return 2;
		} else if (letter == 'f' || letter == 'j' || letter == 'l' || letter == 'm' || letter == 'n') {
			return 3;
		} else if (letter == 'z' || letter == 'x' || letter == 'k' || letter == 'w' || letter == 'p') {
			return 4;
		} else {
			return 5;
		}
		//b 新的
		/*if ((letter >= '0' && letter <= '9')) {
			return 0;
		} else if (letter == 'a' || letter == 'b' || letter == 'c' || letter == 'd' || letter == 'e' || letter == 'f'|| letter == 'g') {
			return 2;
		} else if (letter == 'h' || letter == 'i' || letter == 'j' || letter == 'k' || letter == 'l'|| letter == 'm'|| letter == 'n') {
			return 3;
		} else if (letter == 'o' || letter == 'p' || letter == 'q' || letter == 'r' || letter == 's'|| letter == 't'|| letter == 'u') {
			return 4;
		} else {
			return 5;
		}*/
	}

	public boolean findAddress(String key) throws IOException {
		String result = key.replaceAll("-", "").toLowerCase();
		return findAddress(toIndexAddress(result), result);
	}
/*
	private IntHashMap queryCaches = new IntHashMap();

	private static boolean findStream(byte[] key, FileChannel inChannel, ByteBuffer buffer) throws IOException {
		int patternOffset = 0;
		int len = key.length;
		int bytesRead = inChannel.read(buffer);
		while (bytesRead != -1) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				if (patternOffset < len && key[patternOffset] == buffer.get()) {
					patternOffset++;
					if (patternOffset == len) {
						return true;
					}
				} else {
					patternOffset = 0;
				}
			}
			buffer.clear();
			bytesRead = inChannel.read(buffer);
		}
		return false;
	}*/

	//private ByteBuffer buf = ByteBuffer.allocate(8192*12);

	private boolean findAddress(String hashPath, String key) throws IOException {
		File file = new File(hashPath);
		boolean flag = file.exists();

	/*	if (flag) {
			try {
				FileInputStream in = new FileInputStream(file);
				FileChannel inChannel = in.getChannel();
				flag = findStream(zipString(key).getBytes(), inChannel, buf);
			//	inChannel.close();
				in.close();
			} catch (Exception e) {
				return flag;
			}
		}
		return flag;*/
		if
		  (flag) { BufferedInputStream in = new BufferedInputStream(new
		  FileInputStream(file), 2048); flag =
		  findStream(zipString(key).getBytes(), in); in.close(); } return flag;
		 
	}

	static boolean findStream(byte[] dst, InputStream is) throws IOException {
		int patternOffset = 0;
		int len = dst.length;
		int b = is.read();
		for (; b != -1;) {
			if (dst[patternOffset] == b) {
				patternOffset++;
				if (patternOffset == len) {
					return true;
				}
			} else {
				patternOffset = 0;
			}
			b = is.read();
		}
		return false;
	}

	private final String zipString(String str) {
		if (isMode) {
			int len = str.length();
			return len > 16 ? str.substring(len / 2, len) : str;
		} else {
			return str;
		}
	}

	public boolean findBlockAddress(String key) throws IOException {
		String result = key.replaceAll("-", "").toLowerCase();
		return findBlockAddress(toIndexAddress(result), result);
	}

	protected boolean findBlockAddress(String hashPath, String key) throws IOException {
		File file = new File(hashPath);
		if (!file.exists()) {
			return false;
		}
		byte[] buffers = AddressIndexBlock.findBlock(file, key);
		if (buffers == null || buffers.length == 0) {
			return false;
		}
		return NativeSupport.findCoinAddress(key.getBytes(), buffers);
	}

	protected boolean putAddress(String hashPath, String key) {
		if (isOnlyLocked) {
			try {
				if (findAddress(hashPath, key)) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		synchronized (pPutAddress) {
			try {
				if (pPutAddress.size() > pMaxCache) {
					submit();
				}
				FileOutputStream out = pPutAddress.get(hashPath);
				if (out == null) {
					File file = new File(hashPath);
					if (!file.exists()) {
						FileUtils.makedirs(file);
					}
					out = new FileOutputStream(file, true);
					pPutAddress.put(hashPath, out);
				}
				FileChannel fcOut = out.getChannel();
				if (!fcOut.isOpen()) {
					pPutAddress.remove(hashPath);
					File file = new File(hashPath);
					if (!file.exists()) {
						FileUtils.makedirs(file);
					}
					out = new FileOutputStream(file, true);
					fcOut = out.getChannel();
					pPutAddress.put(hashPath, out);
				}
				fcOut.write(ByteBuffer.wrap((key + LSystem.LS).getBytes()));
			} catch (Exception ex) {
				return false;
			}
			return true;
		}
	}

	public boolean putAddress(String key) {
		String result = key.replaceAll("-", "").toLowerCase();
		return putAddress(toIndexAddress(result), zipString(result));
	}

	public void submit() throws IOException {
		for (FileOutputStream out : pPutAddress.values()) {
			FileChannel fcOut = out.getChannel();
			if (fcOut.isOpen()) {
				fcOut.close();
			}
			out.flush();
			out.close();

		}
		pPutAddress.clear();
	}

}
