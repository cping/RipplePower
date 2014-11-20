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

	private HashMap<String, FileWriter> pPutAddress = new HashMap<String, FileWriter>(
			1000);

	private boolean isOnlyLocked = false;

	protected final static String pIndexName = "index";

	private int pMaxCache = 300;

	private String pDirPath = "";

	public AddressDataBase(String dir) {
		this.pDirPath = dir;
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

	protected final String toIndexAddress(String index_dir) {
		char[] chars = index_dir.toCharArray();
		StringBuilder sbr = new StringBuilder(pDirPath);
		sbr.append(LSystem.FS);
		sbr.append(chars[0]);
		sbr.append(chars[1]);
		sbr.append(LSystem.FS);
		sbr.append(chars[2]);
		sbr.append(LSystem.FS);
		sbr.append(pIndexName);
		return sbr.toString();
	}

	public boolean findAddress(String key) throws IOException {
		return findAddress(toIndexAddress(key.toLowerCase()), key);
	}

	protected boolean findAddress(String hashPath, String key)
			throws IOException {
		File file = new File(hashPath);
		if (!file.exists()) {
			return false;
		}
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		boolean flag = findStream(key.getBytes(), in);
		in.close();
		return flag;
	}

	public boolean findBlockAddress(String key) throws IOException {
		return findBlockAddress(toIndexAddress(key.toLowerCase()), key);
	}

	protected boolean findBlockAddress(String hashPath, String key)
			throws IOException {
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

	private static boolean findStream(byte[] dst, InputStream is)
			throws IOException {
		int patternOffset = 0;
		int len = dst.length;
		int b = is.read();
		for (; b != -1;) {
			if (dst[patternOffset] == ((byte) b)) {
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
		return putAddress(toIndexAddress(key.toLowerCase()), key);
	}

	public void submit() throws IOException {
		for (FileWriter out : pPutAddress.values()) {
			out.flush();
			out.close();
		}
		pPutAddress.clear();
	}

}
