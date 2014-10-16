package org.address.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.address.NativeSupport;
import org.ripple.power.config.Session;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.StringUtils;

public class RippleToDataBase {

	public static AddressManager go(String userFile, String baseDir)
			throws IOException {
		return go(new File(userFile), baseDir);
	}

	public static AddressManager go(File userFile, String baseDir)
			throws IOException {
		HashSet<String> caches = new HashSet<String>(10000);
		AddressManager manager = new AddressManager(baseDir);
		BufferedReader reader = new BufferedReader(new FileReader(userFile));
		String text = null;
		for (int i = 0; (text = reader.readLine()) != null; i++) {
			if (text.indexOf(" ") != -1) {
				String[] result = StringUtils.split(text, " ");
				switch (result.length) {
				case 5:
					String tmp = result[1];
					if (caches.add(tmp)) {
						manager.put(tmp);
					}
					if (caches.size() > 10000) {
						caches.clear();
					}
					if (i % 1000 == 0) {
						System.out
								.println(String.format("Has been read %s", i));
					}
					break;
				default:
					break;
				}
			} else {
				if (caches.add(text)) {
					manager.put(text);
				}
				if (caches.size() > 10000) {
					caches.clear();
				}
				if (i % 1000 == 0) {
					System.out.println(String.format("Has been read %s", i));
				}
			}
		}
		reader.close();
		reader = null;
		manager.submit();
		return manager;
	}

	public static void main(String[] args) throws IOException {
		AddressManager base = new AddressManager("f:\\rippledatabase");
		ArrayList<String> files = FileUtils.getAllFiles("f:\\nxtt_table");
		for (String file : files) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "utf-8"));
			String text = null;
			String[] files1 = StringUtils.split(file, "\\");
			Session session = new Session("ripple_md5" + files1[files1.length - 1]
					+ files1[files1.length - 2] + files1[files1.length - 3]
					+ files1[files1.length - 4]);
			int max = session.getInt("save") == -1 ? 0 : session.getInt("save");
			for (int j = 0; (text = reader.readLine()) != null; j++) {
				if (j < max || text.length() == 0) {
					continue;
				}
				String[] result = text.split(",");
				if (result.length > 1&&result[1].length()>0) {
					String password = NativeSupport
							.getRipplePrivateKey(result[1]);
					String r = password.split(",")[0];
					if (j % 1000 == 0) {
						System.out.println(file + ":" + result[1] + "," + j);
						session.set("save", j);
						session.save();
					}
					if (base.findBlock(r)) {
						System.out.println("万万没想到" + password);
						FileUtils.write(new File(
								"d://ripple_save_dic_data2.txt"), password
								+ "\n", true);
					}
				}
			}

			reader.close();
		}
	}

}
