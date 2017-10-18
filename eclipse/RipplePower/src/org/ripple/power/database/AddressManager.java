package org.ripple.power.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import org.ripple.power.NativeSupport;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class AddressManager {

	private WeakHashMap<String, AddressDataBase> pCaches = new WeakHashMap<String, AddressDataBase>(100000);

	private final static String default_alphabet = "abcdefghijklmnopqrstuvwxyz123456789";

	private String data_base_dir;

	private final AddressIndexBlock pBlock;

	private final String pAlphabet;

	private boolean isOnlyLocked = false, isMode = false;

	public AddressManager(String baseDir) throws IOException {
		this(default_alphabet, baseDir, false);
	}

	public AddressManager(String baseDir, boolean m) throws IOException {
		this(default_alphabet, baseDir, m);
	}

	public AddressManager(String alphabet, String baseDir, boolean m) throws IOException {
		this.data_base_dir = baseDir;
		this.isMode = m;
		if (!data_base_dir.endsWith(LSystem.FS)) {
			data_base_dir += LSystem.FS;
		}
		File tmp = new File(data_base_dir + "address");
		this.pBlock = new AddressIndexBlock(tmp);
		this.pAlphabet = alphabet;
		if (tmp.exists()) {
			pBlock.open();
		} else {
			if (!tmp.exists()) {
				FileUtils.makedirs(tmp);
				tmp = null;
			}
			char[] chars = pAlphabet.toCharArray();
			int length = pAlphabet.length();
			int size = length / 8;
			StringBuilder sbr = new StringBuilder();
			int count = 0;
			for (int i = 0; i < chars.length; i++) {
				sbr.append(chars[i]);
				count++;
				if (count > size) {
					String dir = sbr.toString();
					File file = new File(data_base_dir + LSystem.FS + dir);
					if (!file.exists()) {
						FileUtils.makedirs(file);
						file.mkdirs();
					}
					pBlock.put(dir, file.getAbsolutePath());
					sbr.delete(0, sbr.length());
					count = 0;
				}
			}
			if (sbr.length() > 0) {
				String dir = sbr.toString();
				File file = new File(data_base_dir + LSystem.FS + dir);
				if (!file.exists()) {
					FileUtils.makedirs(file);
					file.mkdirs();
				}
				pBlock.put(dir, file.getAbsolutePath());
			}
			pBlock.save();
		}

	}

	public boolean isOnlyLocked() {
		return isOnlyLocked;
	}

	public void setOnlyLocked(boolean locked) {
		this.isOnlyLocked = locked;
		for (AddressDataBase data : pCaches.values()) {
			if (data != null) {
				data.setMode(isMode);
				data.setOnlyLocked(locked);
			}
		}
	}

	public String getDir() {
		return data_base_dir;
	}

	public boolean put(String key) {
		String dir = pBlock.find(Character.toLowerCase(key.charAt(1)));
		if (dir == null) {
			throw new RuntimeException("Branch path does not exist, check whether the database creation error!");
		}
		AddressDataBase database = pCaches.get(dir);
		if (database == null) {
			database = new AddressDataBase(dir);
			pCaches.put(dir, database);
		}
		database.setMode(this.isMode);
		return database.putAddress(key);
	}

	private AddressDataBase dataBase;

	public boolean find(String key) throws IOException {
		String dir = pBlock.find(Character.toLowerCase(key.charAt(1)));
		if (dir == null) {
			throw new RuntimeException("Branch path does not exist, check whether the database creation error!");
		}
		if (dataBase == null) {
			dataBase = new AddressDataBase(dir);
		} else {
			dataBase.setDirPath(dir);
		}
		dataBase.setMode(isMode);
		return dataBase.findAddress(key);
	}

	public boolean findBlock(String key) throws IOException {
		String dir = pBlock.find(Character.toLowerCase(key.charAt(1)));
		if (dir == null) {
			throw new RuntimeException("Branch path does not exist, check whether the database creation error!");
		}
		AddressDataBase database = pCaches.get(dir);
		if (database == null) {
			database = new AddressDataBase(dir);
			pCaches.put(dir, database);
		}
		return database.findBlockAddress(key);
	}

	public void submit() throws IOException {
		for (AddressDataBase data : pCaches.values()) {
			if (data != null) {
				data.setMode(isMode);
				data.submit();
			}
		}
	}

	protected static File createTempFile(String tmpFile) {
		try {
			final File tempFile = File.createTempFile(tmpFile, "tmp");
			return tempFile;
		} catch (final IOException e) {
			throw new RuntimeException("Unable to create tempfile for unit test", e);
		}

	}

	protected static File createTempDirectory() {
		final File directory = findTempDirectory();
		directory.mkdir();
		return directory;
	}

	protected static File findTempDirectory() {
		final File directory = createTempFile(String.valueOf(System.currentTimeMillis()));
		directory.delete();
		return directory;
	}

	public void clearRepeatData() throws IOException {
		ArrayList<String> listfile = FileUtils.getAllFiles(data_base_dir);
		for (String file : listfile) {
			if (file.endsWith(AddressDataBase.pIndexName)) {
				System.out.println(String.format("Repeat Process %s", file));
				File path = new File(file);
				BufferedReader bf = new BufferedReader(new FileReader(path));
				HashSet<String> contactSet = new HashSet<String>();
				String line;
				File newFile = createTempFile(System.currentTimeMillis() + path.getPath());
				if (!newFile.exists()) {
					FileUtils.makedirs(newFile);
				}
				FileWriter writer = new FileWriter((newFile), false);
				while ((line = bf.readLine()) != null) {
					if (!contactSet.contains(line)) {
						writer.write(line);
						writer.write("\n");
						contactSet.add(line);
					}
				}
				bf.close();
				writer.flush();
				writer.close();
				path.delete();
				newFile.renameTo(path);
			}
		}
	}

	public void updateDataToBlock() throws IOException {
		ArrayList<String> listfile = FileUtils.getAllFiles(data_base_dir);
		for (String file : listfile) {
			if (file.endsWith(AddressDataBase.pIndexName)) {
				System.out.println(String.format("Block Process %s", file));
				File path = new File(file);
				BufferedReader bf = new BufferedReader(new FileReader(path));
				String line;
				File newFile = createTempFile(System.currentTimeMillis() + path.getPath());
				if (!newFile.exists()) {
					FileUtils.makedirs(newFile);
				}
				HashMap<String, HashMap<String, ArrayList<String>>> tmps = new HashMap<String, HashMap<String, ArrayList<String>>>(
						10000);
				while ((line = bf.readLine()) != null) {
					if (line.length() > 4) {
						String key = String.valueOf(Character.toLowerCase(line.charAt(3)));
						HashMap<String, ArrayList<String>> maps = tmps.get(key);
						String newKey = String.valueOf(Character.toLowerCase(line.charAt(4)));
						if (maps == null) {
							maps = new HashMap<String, ArrayList<String>>(10000);
							tmps.put(key, maps);
						}
						ArrayList<String> list = maps.get(newKey);
						if (list == null) {
							list = new ArrayList<String>(10000);
							maps.put(newKey, list);
						}
						if (!list.contains(line)) {
							list.add(line);
						}

					}
				}
				bf.close();
				path.delete();
				AddressIndexBlock.putBlock(newFile.getAbsolutePath(), tmps);
				newFile.renameTo(path);
				tmps.clear();
				tmps = null;
			}
		}
	}

}
