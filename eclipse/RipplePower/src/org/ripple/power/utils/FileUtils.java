/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package org.ripple.power.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ripple.power.collection.ArrayByte;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIRes;

final public class FileUtils {

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context) throws IOException {
		write(fileName, context, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(File file, String context, String coding) throws IOException {
		write(file, context.getBytes(coding), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context, boolean append) throws IOException {
		write(new File(fileName), context.getBytes(LSystem.encoding), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param bytes
	 * @throws IOException
	 */
	public static void write(File file, byte[] bytes) throws IOException {
		write(file, new ByteArrayInputStream(bytes), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param bytes
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, byte[] bytes, boolean append) throws IOException {
		write(file, new ByteArrayInputStream(bytes), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param input
	 * @throws IOException
	 */
	public static void write(File file, InputStream input) throws IOException {
		write(file, input, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param input
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, InputStream input, boolean append) throws IOException {
		makedirs(file);
		BufferedOutputStream output = null;
		try {
			int contentLength = input.available();
			output = new BufferedOutputStream(new FileOutputStream(file, append));
			while (contentLength-- > 0) {
				output.write(input.read());
			}
		} finally {
			close(input);
			close(output);
		}
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param chars
	 * @throws IOException
	 */
	public static void write(File file, char[] chars) throws IOException {
		write(file, new CharArrayReader(chars), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param chars
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, char[] chars, boolean append) throws IOException {
		write(file, new CharArrayReader(chars), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param string
	 * @throws IOException
	 */
	public static void write(File file, String string) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param string
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, String string, boolean append) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param reader
	 * @throws IOException
	 */
	public static void write(File file, Reader reader) throws IOException {
		write(file, reader, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param reader
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, Reader reader, boolean append) throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			int i = -1;
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}

		} finally {
			close(reader);
			close(writer);
		}
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param records
	 * @throws IOException
	 */
	public static void write(File file, ArrayList<String> records) throws IOException {
		write(file, records, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param records
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, ArrayList<String> records, boolean append) throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			for (Iterator<String> it = records.iterator(); it.hasNext();) {
				writer.write(it.next());
				writer.write(LSystem.LS);
			}
		} finally {
			close(writer);
		}
	}

	public static void write(File file, HashSet<String> records, boolean append) throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			for (Iterator<String> it = records.iterator(); it.hasNext();) {
				writer.write(it.next());
				writer.write(LSystem.LS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(writer);
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void makedirs(String fileName) throws IOException {
		makedirs(new File(fileName));

	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 * @return
	 */
	public static void makedirs(File file) throws IOException {
		checkFile(file);
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Creating directories " + file + "," + parentFile.getPath() + " failed.");
			}
		}
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param file
	 * @throws IOException
	 */
	private static void checkFile(File file) throws IOException {
		boolean exists = file.exists();
		if (exists && !file.isFile()) {
			throw new IOException("File " + file.getPath() + " is actually not a file.");
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param input
	 * @param file
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param output
	 * @param file
	 */
	public static void close(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param reader
	 * @param file
	 */
	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param writer
	 * @param file
	 */
	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象产生异常
	 * 
	 * @param file
	 * @param e
	 */
	public static void closingFailed(IOException e) {
		throw new RuntimeException(e.getMessage());
	}

	/**
	 * 拷贝指定长度数据流
	 * 
	 * @param is
	 * @param os
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream is, OutputStream os, long len) throws IOException {
		byte[] buf = new byte[1024];
		long copied = 0;
		int read;
		while ((read = is.read(buf)) != 0 && copied < len) {
			long leftToCopy = len - copied;
			int toWrite = read < leftToCopy ? read : (int) leftToCopy;
			os.write(buf, 0, toWrite);
			copied += toWrite;
		}
		return copied;
	}

	/**
	 * 拷贝指定数据流
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException {
		long written = 0;
		byte[] buffer = new byte[4096];
		while (true) {
			int len = in.read(buffer);
			if (len < 0) {
				break;
			}
			out.write(buffer, 0, len);
			written += len;
		}
		return written;
	}

	/**
	 * 读取指定长度数据流
	 * 
	 * @param is
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(InputStream is, long len) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(is, out, len);
		return out.toByteArray();
	}

	/**
	 * 获得指定文件大小
	 * 
	 * @param file
	 * @return
	 */
	public static long getKB(File file) {
		return getKB(file.length());
	}

	/**
	 * 将指定长度转化为KB显示
	 * 
	 * @param size
	 * @return
	 */
	public static long getKB(long size) {
		size /= 1000L;
		if (size == 0L) {
			size = 1L;
		}
		return size;
	}

	/**
	 * 删除指定目录下全部文件
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteAll(File dir) {
		String fileNames[] = dir.list();
		if (fileNames == null)
			return false;
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File(dir, fileNames[i]);
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				deleteAll(file);
		}

		return dir.delete();
	}

	/**
	 * 读取file文件,转为byte[]
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		InputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			extracted(file);
		}
		is.close();
		return bytes;
	}

	private static void extracted(File file) throws IOException {
		throw new IOException("Could not completely read file " + file.getName());
	}

	/**
	 * 读取指定文件为Byte[]
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] readBytesFromFile(String fileName) {
		try {
			return readBytesFromFile(new File(fileName));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 读取file
	 * 
	 * @param file
	 * @return
	 */
	public static final InputStream read(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public static final ArrayList<String> readListT(String fileName) {
		ArrayList<String> list = new ArrayList<String>(1000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String result = null;
			for (; (result = reader.readLine()) != null;) {
				if (!StringUtils.isEmpty(result)) {
					list.add(result.trim().toLowerCase());
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static final ArrayList<String> readList(String fileName) {
		ArrayList<String> list = new ArrayList<String>(1000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String result = null;
			for (; (result = reader.readLine()) != null;) {
				if (!StringUtils.isEmpty(result)) {
					list.add(result.trim());
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public static final HashSet<String> readSet(String fileName) {
		HashSet<String> list = new HashSet<String>(1000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String result = null;
			for (; (result = reader.readLine()) != null;) {
				if (!StringUtils.isEmpty(result)) {
					list.add(result.trim());
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public static final ArrayList<String> readList(String fileName, int start, int end) {
		ArrayList<String> list = new ArrayList<String>(1000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String result = null;
			for (; (result = reader.readLine()) != null;) {
				if (!StringUtils.isEmpty(result)) {
					list.add(result.trim().substring(start, end));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static final String readAsText(InputStream input, String charset) throws IOException {
		Reader rd;
		if (null == charset) {
			rd = new InputStreamReader(input);
		} else {
			rd = new InputStreamReader(input, charset);
		}
		StringBuilder buf = new StringBuilder();
		int c = rd.read();
		for (; c != -1;) {
			buf.append((char) c);
			c = rd.read();
		}
		rd.close();
		return buf.toString();
	}

	public static final String readAsText(InputStream input) throws IOException {
		return readAsText(input, "UTF-8");
	}

	/**
	 * 以指定全路径名读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static final InputStream read(String fileName) {
		return read(new File(fileName));
	}

	/**
	 * 获得指定路径下的所有文件名(包含全路径)
	 * 
	 * @param path
	 *            String 指定目录
	 * @return ArrayList 所有文件名(包含全路径)
	 * @throws IOException
	 */
	public static HashSet<String> getAllFiles(String path) throws IOException {
		File file = new File(path);
		HashSet<String> ret = new HashSet<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					HashSet<String> arr = getAllFiles(tempfile.getPath());
					ret.addAll(arr);
					arr.clear();
					arr = null;
				} else {
					ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径下的所有目录(包含全路径)
	 * 
	 * @param path
	 *            String 指定目录
	 * @return ArrayList 所有目录(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					ret.add(tempfile.getAbsolutePath());
					ArrayList<String> arr = getAllDir(tempfile.getPath());
					ret.addAll(arr);
					arr.clear();
					arr = null;

				}
			}
		}
		return ret;

	}

	/**
	 * 获得指定路径下指定扩展名的所有文件(包含全路径)
	 * 
	 * @param path
	 *            String 指定路径
	 * @param ext
	 *            String 扩展名
	 * @return ArrayList 所有文件(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllFiles(String path, String ext) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			return new ArrayList<String>(0);
		}
		ArrayList<String> ret = new ArrayList<String>();
		String[] exts = ext.split(",");
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					ArrayList<String> arr = getAllFiles(tempfile.getPath(), ext);
					ret.addAll(arr);
					arr.clear();
					arr = null;
				} else {
					for (int j = 0; j < exts.length; j++) {
						if (getExtension(tempfile.getAbsolutePath()).equalsIgnoreCase(exts[j])) {
							ret.add(tempfile.getAbsolutePath());
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 读取本地或网络文件，并返回文件流和文件长度
	 * 
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("resource")
	public static Object[] readPath(String fileName) {
		try {
			int len = 0;
			File file = new File(fileName);
			InputStream is = null;
			if (file.exists()) {
				is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				len = (int) file.length();
			} else {
				is = UIRes.getStream(fileName);
				if (is != null) {
					if (is.available() > 0) {
						len = is.available();
					} else {
						len = new ArrayByte(is, ArrayByte.BIG_ENDIAN).length();
					}
				} else {
					URL url = new URL(fileName);
					URLConnection uc = url.openConnection();
					uc.setUseCaches(true);
					if (fileName.endsWith(".zip")) {
						try {
							is = new ZipInputStream(new BufferedInputStream(uc.getInputStream(), 8192));
							ZipEntry ze = ((ZipInputStream) is).getNextEntry();
							len = (int) ze.getSize();
						} catch (Exception ex) {
							is = null;
						}
					}
					if (is == null) {
						len = uc.getContentLength();
						is = new DataInputStream(new BufferedInputStream(uc.getInputStream(), 8192));
					}
				}

			}
			return new Object[] { is, Integer.valueOf(len) };
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获得指定路径下的文件列表(包含全路径),仅包含一级目录
	 * 
	 * @param path
	 *            String 指定路径
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> Ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

				if (!tempfile.isDirectory()) {
					Ret.add(tempfile.getAbsolutePath());

				}

			}
		}
		return Ret;
	}

	/**
	 * 获得指定路径下的子目录(包含全路径),仅包含一级目录
	 * 
	 * @param path
	 *            String 指定路径
	 * @return ArrayList 子目录
	 * @throws IOException
	 */
	public static ArrayList<String> getDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

				if (tempfile.isDirectory()) {
					ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径下指定扩展名的文件(包含全路径),仅包含一级目录
	 * 
	 * @param path
	 *            String 指定路径
	 * @param ext
	 *            String 扩展名
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path, String ext) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

				if (!tempfile.isDirectory()) {
					if (getExtension(tempfile.getAbsolutePath()).equalsIgnoreCase(ext))
						ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径的目录名
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileName(String name) {
		if (name == null) {
			return "";
		}
		if (name.indexOf(LSystem.FS) != -1) {
			int length = name.length();
			int size = name.lastIndexOf(LSystem.FS) + 1;
			if (size < length) {
				return name.substring(size, length);
			} else {
				return "";
			}
		} else {
			int length = name.length();
			int size = name.lastIndexOf("/") + 1;
			if (size < length) {
				return name.substring(size, length);
			} else {
				return "";
			}
		}
	}

	public static String getLastDirName(String name) {
		if (name == null) {
			return "";
		}
		if (name.indexOf("\\") != -1) {
			String[] list = StringUtils.splitNoCoalesce(name, '\\');
			if (list.length > 1) {
				return list[list.length - 2];
			}
			return list[list.length - 1];
		} else if (name.indexOf("/") != -1) {
			String[] list = StringUtils.splitNoCoalesce(name, '/');
			if (list.length > 1) {
				return list[list.length - 2];
			}
			return list[list.length - 1];

		}
		return name;
	}

	/**
	 * 获得指定文件扩展名
	 * 
	 * @param name
	 * @return
	 */
	public static String getExtension(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(index + 1);
		}
	}

	public static String getExtension2(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(0, index);
		}
	}

	/**
	 * 删除指定目录下的所有文件
	 * 
	 * @param path
	 *            String 指定目录
	 * @throws Exception
	 */
	public static void deleteFile(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				// 如果是目录
				if (tempfile.isDirectory()) {
					deleteFile(tempfile.getPath());
				} else { // 如果不是
					tempfile.delete();

				}
			}
		}
	}

	/**
	 * 删除指定目录下的所有目录
	 * 
	 * @param path
	 *            String 指定目录
	 * @throws Exception
	 */
	public static void deleteDir(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					deleteDir(tempfile.getPath());
					tempfile.delete();

				}
			}
		}
	}
}
