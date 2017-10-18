package org.ripple.power.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.ripple.power.utils.StringUtils;

public class RippleToDataBase {

	public static AddressManager go(String userFile, String baseDir) throws IOException {
		return go(new File(userFile), baseDir);
	}

	public static AddressManager go(File userFile, String baseDir) throws IOException {
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
						System.out.println(String.format("Has been read %s", i));
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

}
