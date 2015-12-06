package org.ripple.power.ui.projector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.ripple.power.collection.ArrayByte;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.StringUtils;

public abstract class Resources {

	final static private Object LOCK = new Object();

	private final static HashMap<String, Object> lazyResources = new HashMap<String, Object>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private Resources() {
	}

	/**
	 * 获得资源名迭代器
	 * 
	 * @return
	 */
	public static Iterator<String> getNames() {
		synchronized (LOCK) {
			return lazyResources.keySet().iterator();
		}
	}

	/**
	 * 检查指定资源名是否存在
	 * 
	 * @param resName
	 * @return
	 */
	public static boolean contains(String resName) {
		synchronized (LOCK) {
			return (lazyResources.get(resName) != null);
		}
	}

	/**
	 * 删除指定名称的资源
	 * 
	 * @param resName
	 */
	public static void remove(String resName) {
		synchronized (LOCK) {
			lazyResources.remove(resName);
		}
	}

	/**
	 * 通过url读取网络文件流
	 * 
	 * @param uri
	 * @return
	 */
	final static public byte[] getHttpStream(final String uri) {
		URL url;
		try {
			url = new URL(uri);
		} catch (Exception e) {
			return null;
		}
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (Exception e) {
			return null;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] arrayByte = null;
		try {
			arrayByte = new byte[4096];
			int read;
			while ((read = is.read(arrayByte)) >= 0) {
				os.write(arrayByte, 0, read);
			}
			arrayByte = os.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (os != null) {
					os.close();
					os = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}

		return arrayByte;
	}

	/**
	 * 读取指定资源为InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getResourceAsStream(final String fileName) {
		if ((fileName.indexOf("file:") >= 0) || (fileName.indexOf(":/") > 0)) {
			try {
				URL url = new URL(fileName);
				return new BufferedInputStream(url.openStream());
			} catch (Exception e) {
				return null;
			}
		}
		return new ByteArrayInputStream(getResource(fileName).getData());
	}

	/**
	 * 读取指定资源为InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getNotCacheResourceAsStream(final String fileName) {
		if ((fileName.indexOf("file:") >= 0) || (fileName.indexOf(":/") > 0)) {
			try {
				URL url = new URL(fileName);
				return new BufferedInputStream(url.openStream());
			} catch (Exception e) {
				return null;
			}
		}
		return new ByteArrayInputStream(getNotCacheResource(fileName).getData());
	}

	public static InputStream openResource(final String resName)
			throws IOException {
		File file = new File(resName);
		if (file.exists()) {
			try {
				return new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new IOException(resName + " file not found !");
			}
		} else {
			InputStream in = null;
			try {
				in = UIRes.getStream(resName);
			} catch (Exception e) {
				throw new RuntimeException(resName + " not found!");
			}
			return in;
		}
	}

	private static boolean isExists(String fileName) {
		return new File(fileName).exists();
	}

	/**
	 * 将指定文件转为ArrayByte
	 * 
	 * @param fileName
	 * @return
	 */
	public static ArrayByte getResource(final String fileName) {
		String innerName = fileName;
		String keyName = innerName.replaceAll(" ", "").toLowerCase();
		synchronized (LOCK) {
			if (lazyResources.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
				lazyResources.clear();
				System.gc();
			}
			byte[] data = (byte[]) lazyResources.get(keyName);
			if (data != null) {
				return new ArrayByte(data);
			}
		}
		BufferedInputStream in = null;
		boolean canInner = innerName.startsWith(".")
				|| (innerName.startsWith("/") && LSystem.isWindows());
		if (!isExists(innerName) && !canInner) {
			innerName = ("/" + innerName).intern();
			canInner = true;
		}
		if (canInner) {
			if (innerName.startsWith(".")) {
				innerName = innerName.substring(1, innerName.length());
			}
			innerName = StringUtils.replaceIgnoreCase(innerName, "\\", "/");
			innerName = innerName.substring(1, innerName.length());
		} else {
			if (innerName.startsWith("\\")) {
				innerName = innerName.substring(1, innerName.length());
			}
		}
		if (!canInner) {
			try {
				in = new BufferedInputStream(new FileInputStream(new File(
						innerName)));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		} else {
			try {
				in = new BufferedInputStream(UIRes.getStream(innerName));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			in.close();
			byteArray.reset();
			lazyResources.put(keyName, byteArray.getData());
			return byteArray;
		} catch (IOException ex) {
			throw new RuntimeException(fileName + " file not found !");
		}
	}

	public static ArrayByte getNotCacheResource(final String fileName) {
		String innerName = fileName;
		BufferedInputStream in = null;
		boolean canInner = innerName.startsWith(".")
				|| (innerName.startsWith("/") && LSystem.isWindows());
		if (!isExists(innerName) && !canInner) {
			innerName = ("/" + innerName).intern();
			canInner = true;
		}
		if (canInner) {
			if (innerName.startsWith(".")) {
				innerName = innerName.substring(1, innerName.length());
			}
			innerName = StringUtils.replaceIgnoreCase(innerName, "\\", "/");
			innerName = innerName.substring(1, innerName.length());
		} else {
			if (innerName.startsWith("\\")) {
				innerName = innerName.substring(1, innerName.length());
			}
		}
		if (!canInner) {
			try {
				in = new BufferedInputStream(new FileInputStream(new File(
						innerName)));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		} else {
			try {
				in = new BufferedInputStream(UIRes.getStream(innerName));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			in.close();
			byteArray.reset();
			return byteArray;
		} catch (IOException ex) {
			throw new RuntimeException(fileName + " file not found !");
		}
	}

	/**
	 * 将InputStream转为byte[]
	 * 
	 * @param is
	 * @return
	 */
	final static public byte[] getDataSource(InputStream is) {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[8192];
		try {
			int read;
			while ((read = is.read(bytes)) >= 0) {
				byteArrayOutputStream.write(bytes, 0, read);
			}
			bytes = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.flush();
					byteArrayOutputStream = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}
		return bytes;
	}

	public static void destroy() {
		lazyResources.clear();
	}

	public void finalize() {
		destroy();
	}
}
