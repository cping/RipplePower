package org.ripple.power.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ripple.power.config.LSystem;

public class AddressIndexBlock {

	protected File pDataBaseFile;
	protected final int MAXLENGTH = 1 << 20;
	protected HashMap<String, String> pDict = new HashMap<String, String>();

	public AddressIndexBlock(String path) {
		this(new File(path));
	}

	public AddressIndexBlock(File path) {
		this.pDataBaseFile = path;
	}

	public File getFile() {
		return this.pDataBaseFile;
	}

	private void checkValid() {
		if (pDataBaseFile == null) {
			throw new RuntimeException("DataBase pointer was null");
		}
	}

	private static byte[] output(HashMap<String, ArrayList<String>> keys)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
		DataOutputStream db = new DataOutputStream(out);
		for (String name : keys.keySet()) {
			byte[] key = name.getBytes(LSystem.encoding);
			StringBuilder sbr = new StringBuilder();
			for (String val : keys.get(name)) {
				sbr.append(val);
				sbr.append("\n");
			}
			byte[] value = sbr.toString().getBytes(LSystem.encoding);
			db.writeInt(key.length);
			db.writeInt(value.length);
			db.write(key);
			db.write(value);
		}
		byte[] bytes = out.toByteArray();
		out.close();
		return bytes;
	}

	protected static void putBlock(String tableFile,
			HashMap<String, HashMap<String, ArrayList<String>>> keys)
			throws IOException {
		try (DataOutputStream db = new DataOutputStream(new FileOutputStream(
				tableFile))) {
			for (String name : keys.keySet()) {
				byte[] key = name.getBytes(LSystem.encoding);
				ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
				byte[] value = output(keys.get(name));
				out.close();
				db.writeInt(key.length);
				db.writeInt(value.length);
				db.write(key);
				db.write(value);
			}
		} catch (IOException e) {
			throw new IOException(String.format(
					"Conformity saving: IOException: %s", e.getMessage()));
		}
	}

	private static byte[] input(byte[] ins, String name) throws IOException {
		int find = Character.toLowerCase(name.charAt(4));
		ByteArrayInputStream tmp = new ByteArrayInputStream(ins);
		DataInputStream in = new DataInputStream(tmp);
		for (;;) {
			in.readInt();
			int valueLength = in.readInt();
			byte keyBuffer = in.readByte();
			if (keyBuffer == find) {
				byte[] valueBuffer = new byte[valueLength];
				in.read(valueBuffer, 0, valueLength);
				return valueBuffer;
			} else {
				in.skipBytes(valueLength);
			}
		}
	}

	protected static byte[] findBlock(byte[] ins, String name)
			throws IOException {
		DataInputStream db = new DataInputStream(new ByteArrayInputStream(ins));
		int find = Character.toLowerCase(name.charAt(3));
		for (;;) {
			db.readInt();
			int valueLength = db.readInt();
			byte keyBuffer = db.readByte();
			if (keyBuffer == find) {
				byte[] valueBuffer = new byte[valueLength];
				db.read(valueBuffer, 0, valueLength);
				return input(valueBuffer, name);
			} else {
				db.skipBytes(valueLength);
			}
		}
	}

	protected static byte[] findBlock(String tableFile, String name)
			throws IOException {
		try (DataInputStream db = new DataInputStream(new FileInputStream(
				tableFile))) {
			int find = Character.toLowerCase(name.charAt(3));
			for (;;) {
				db.readInt();
				int valueLength = db.readInt();
				byte keyBuffer = db.readByte();
				if (keyBuffer == find) {
					byte[] valueBuffer = new byte[valueLength];
					db.read(valueBuffer, 0, valueLength);
					return input(valueBuffer, name);
				} else {
					db.skipBytes(valueLength);
				}
			}
		} catch (Exception e) {
			return null;
		}
	}
	protected static byte[] findBlock(File tableFile, String name)
			throws IOException {
		try (DataInputStream db = new DataInputStream(new FileInputStream(
				tableFile))) {
			int find = Character.toLowerCase(name.charAt(3));
			for (;;) {
				db.readInt();
				int valueLength = db.readInt();
				byte keyBuffer = db.readByte();
				if (keyBuffer == find) {
					byte[] valueBuffer = new byte[valueLength];
					db.read(valueBuffer, 0, valueLength);
					return input(valueBuffer, name);
				} else {
					db.skipBytes(valueLength);
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void open() throws IOException {
		checkValid();
		if (!pDataBaseFile.exists() || pDataBaseFile.length() == 0) {
			pDict = new HashMap<String, String>();
			return;
		}
		try (DataInputStream db = new DataInputStream(new FileInputStream(
				pDataBaseFile))) {
			while (true) {
				int keyLength;
				try {
					keyLength = db.readInt();
				} catch (EOFException e) {
					break;
				}
				if (keyLength <= 0 || keyLength > MAXLENGTH) {
					throw new Exception(String.format(
							"Key length must be in [1; %d]", MAXLENGTH));
				}
				int valueLength = db.readInt();
				if (valueLength <= 0 || valueLength > MAXLENGTH) {
					throw new Exception(String.format(
							"Value length must be in [1; %d]", MAXLENGTH));
				}
				byte[] keyBuffer = new byte[keyLength];
				db.readFully(keyBuffer, 0, keyLength);
				String key = new String(keyBuffer, LSystem.encoding);
				byte[] valueBuffer = new byte[valueLength];
				db.readFully(valueBuffer, 0, valueLength);
				String value = new String(valueBuffer, LSystem.encoding);
				pDict.put(key, value);
			}
		} catch (IOException e) {
			pDict = new HashMap<String, String>();
			throw new IOException(
					String.format(
							"Conformity loading: IOException: %s. Empty database applied",
							e.getMessage()));
		} catch (Exception e) {
			pDict = new HashMap<String, String>();
			throw new IOException(
					String.format(
							"Conformity loading: Exception: %s. Empty database applied",
							e.getMessage()));
		}
	}

	public void save() throws IOException {
		checkValid();
		try (DataOutputStream db = new DataOutputStream(new FileOutputStream(
				pDataBaseFile))) {
			for (Map.Entry<String, String> entry : pDict.entrySet()) {
				byte[] key = entry.getKey().getBytes(LSystem.encoding);
				byte[] value = entry.getValue().getBytes(LSystem.encoding);
				db.writeInt(key.length);
				db.writeInt(value.length);
				db.write(key);
				db.write(value);
			}
		} catch (IOException e) {
			throw new IOException(String.format(
					"Conformity saving: IOException: %s", e.getMessage()));
		}
	}

	public String put(String key, String value) {
		return pDict.put(key, value);
	}

	public String remove(String key) {
		return pDict.remove(key);
	}

	public String get(String key) {
		return pDict.get(key);
	}

	protected String find(char c) {
		for (String name : pDict.keySet()) {
			if (name.indexOf(c) != -1) {
				return pDict.get(name);
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		/*
		 * AddressIndexBlock addressDataBase=new
		 * AddressIndexBlock("d:\\tablettttttt.txt"); addressDataBase.open();
		 * addressDataBase.put("SSSSSSSSSSSS", "BBBBBBBBBBBBBBBBBBBBBBB");
		 * addressDataBase.save();
		 */

		System.out.println(new String(AddressIndexBlock.findBlock(
				"d:\\tablettttttt.txt", "SSSSSSSSSSSS")));

	}
}
