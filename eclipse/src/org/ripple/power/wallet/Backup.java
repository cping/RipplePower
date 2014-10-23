package org.ripple.power.wallet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.ripple.power.config.LSystem;

public class Backup {

	public static String create() {
		String fileName = LSystem.getDirectory() + LSystem.FS
				+ LSystem.walletName;
		File file = new File(fileName);
		if (file.exists() && file.length() > 0) {
			fileName = file.getName();
			String backupFileName = fileName + "." + System.currentTimeMillis();
			File backupFile = new File(file.getParent(), backupFileName);
			if (backupFile.exists()) {
				int counter = 0;
				do {
					counter++;
					backupFile = new File(file.getParent(), backupFileName
							+ counter);
				} while (backupFile.exists());
			}
			try {
				Files.copy(file.toPath(), backupFile.toPath());
			} catch (IOException e) {
				return null;
			}
			return backupFile.getAbsolutePath();
		}
		return null;
	}

}
