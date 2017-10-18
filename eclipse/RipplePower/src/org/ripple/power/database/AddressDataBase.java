package org.ripple.power.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.ripple.power.NativeSupport;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class AddressDataBase {

	private HashMap<String, FileWriter> pPutAddress = new HashMap<String, FileWriter>(1000);

	private boolean isOnlyLocked = false, isMode = false;

	protected final static String pIndexName = "index";

	private int pMaxCache = 300;

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
		if (chars[0] == 'r') {
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
		sbr.append(chars[2]);
		sbr.append(LSystem.FS);
		if (isMode) {
			sbr.append(getNumber(chars[3]));
			sbr.append(LSystem.FS);
			sbr.append(getNumber(chars[4]));
			sbr.append(LSystem.FS);
			sbr.append(getNumber(chars[5]) + pIndexName);
		} else {
			sbr.append(pIndexName);
		}
		return sbr.toString();
	}

	private final static int getNumber(char letter) {
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
	}

	public boolean findAddress(String key) throws IOException {
		String result = key.replaceAll("-", "").toLowerCase();
		return findAddress(toIndexAddress(result), result);
	}

	private boolean findAddress(String hashPath, String key) throws IOException {
		File file = new File(hashPath);
		if (!file.exists()) {
			return false;
		} else {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), 16384);
			boolean flag = findStream(zipString(key).getBytes(), in);
			in.close();
			return flag;
		}
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

	private static boolean findStream(byte[] dst, InputStream is) throws IOException {
		int patternOffset = 0;
		int len = dst.length;
		int b = is.read();
		for (; b != -1;) {
			if (dst[patternOffset] == (byte) b) {
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

				FileWriter out = pPutAddress.get(hashPath);
				if (out == null) {
					File file = new File(hashPath);
					if (!file.exists()) {
						FileUtils.makedirs(file);
					}

					out = new FileWriter(hashPath, true);
					pPutAddress.put(hashPath, out);
				}
				out.write(key);
				out.write(LSystem.LS);
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
		for (FileWriter out : pPutAddress.values()) {
			out.flush();
			out.close();
		}
		pPutAddress.clear();
	}

}
