package org.ripple.power.config;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.ripple.power.utils.FileUtils;

public class ApplicationInfo {
	private final String applicationName;
	private final File userDir;
	private final File applicationDataDir;

	private static FileChannel channel;
	private static FileLock lock;
	private static File lockfile;

	public ApplicationInfo(String applicationName) {
		super();
		assert applicationName != null;
		this.applicationName = applicationName;
		userDir = new File(LSystem.getUserName());
		if (LSystem.isMacOSX()) {
			applicationDataDir = new File(userDir, "/Library/" + applicationName);
		} else {
			applicationDataDir = new File(userDir, "/" + applicationName);
		}
		try {
			FileUtils.makedirs(applicationDataDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public static boolean lock() {
		try {
			ApplicationInfo info = new ApplicationInfo("power");
			lockfile = new File(info.applicationDataDir.getAbsolutePath() + ".lock");
			channel = new RandomAccessFile(lockfile, "rw").getChannel();
			lock = channel.tryLock();
			if (lock == null) {
				channel.close();
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void unlock() {
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				lockfile.delete();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getApplicationName() {
		return applicationName;
	}

	public File getUserDir() {
		return userDir;
	}

	public File getApplicationDataDir() {
		return applicationDataDir;
	}
}
