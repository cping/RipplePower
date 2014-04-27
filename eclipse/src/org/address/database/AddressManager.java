package org.address.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.address.NativeSupport;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class AddressManager {

	private HashMap<String, AddressDataBase> pCaches = new HashMap<String, AddressDataBase>(
			10000);

	private final static String default_alphabet = "abcdefghijklmnopqrstuvwxyz123456789";

	private String data_base_dir;

	private final AddressIndexBlock pBlock;

	private final String pAlphabet;

	private boolean isOnlyLocked = false;

	public AddressManager(String baseDir) throws IOException {
		this(default_alphabet, baseDir);
	}

	public AddressManager(String alphabet, String baseDir) throws IOException {
		this.data_base_dir = baseDir;
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
			throw new RuntimeException(
					"Branch path does not exist, check whether the database creation error!");
		}
		AddressDataBase database = pCaches.get(dir);
		if (database == null) {
			database = new AddressDataBase(dir);
			pCaches.put(dir, database);
		}
		return database.putAddress(key);
	}

	public boolean find(String key) throws IOException {
		String dir = pBlock.find(Character.toLowerCase(key.charAt(1)));
		if (dir == null) {
			throw new RuntimeException(
					"Branch path does not exist, check whether the database creation error!");
		}
		AddressDataBase database = pCaches.get(dir);
		if (database == null) {
			database = new AddressDataBase(dir);
			pCaches.put(dir, database);
		}
		return database.findAddress(key);
	}

	public boolean findBlock(String key) throws IOException {
		String dir = pBlock.find(Character.toLowerCase(key.charAt(1)));
		if (dir == null) {
			throw new RuntimeException(
					"Branch path does not exist, check whether the database creation error!");
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
				data.submit();
			}
		}
	}

	protected static File createTempFile(String tmpFile) {
		try {
			final File tempFile = File.createTempFile(tmpFile, "tmp");
			return tempFile;
		} catch (final IOException e) {
			throw new RuntimeException(
					"Unable to create tempfile for unit test", e);
		}

	}

	protected static File createTempDirectory() {
		final File directory = findTempDirectory();
		directory.mkdir();
		return directory;
	}

	protected static File findTempDirectory() {
		final File directory = createTempFile(String.valueOf(System
				.currentTimeMillis()));
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
				File newFile = createTempFile(System.currentTimeMillis()
						+ path.getPath());
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
				File newFile = createTempFile(System.currentTimeMillis()
						+ path.getPath());
				if (!newFile.exists()) {
					FileUtils.makedirs(newFile);
				}
				HashMap<String, HashMap<String, ArrayList<String>>> tmps = new HashMap<String, HashMap<String, ArrayList<String>>>(
						10000);
				while ((line = bf.readLine()) != null) {
					if (line.length() > 4) {
						String key = String.valueOf(Character.toLowerCase(line
								.charAt(3)));
						HashMap<String, ArrayList<String>> maps = tmps.get(key);
						String newKey = String.valueOf(Character
								.toLowerCase(line.charAt(4)));
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

	
	public static void main(String[]args) throws IOException{
		AddressManager manager = new AddressManager("F:\\bitcoin_data");
		//manager.clearRepeatData();
		//manager.updateDataToBlock();
		String text=null;
		for(int i=0;;i+=33){
			
			//	System.out.println(text);
				text = String.valueOf(i);
				String address = NativeSupport.getBitcoinPrivateKey(text).split(",")[0];
			//	System.out.println("CCCCCCCC:"+address);
				if(manager.findBlock(address)){
					System.out.println("万万没想到"+address+","+ text);
					FileUtils.write(new File("d://bit_save_random_data23.txt"),
							address+","+ text + "\n", true);
				}
				if(i%5000==0){
					System.out.println(text);
				}

			}
	}
}
