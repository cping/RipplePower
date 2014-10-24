package org.ripple.power.config;

import java.io.File;


public class ApplicationInfo {
	private final String applicationName;
	private final File userDir;
	private final File applicationDataDir;

	public ApplicationInfo(String applicationName) {
		super();
		assert applicationName != null;
		this.applicationName = applicationName;
		userDir = new File(System.getProperty("user.home"));
		if (LSystem.isMacOSX()) {
			applicationDataDir = new File(userDir, "/Library/"
					+ applicationName);
		} else {
			applicationDataDir = new File(userDir, "/." + applicationName);
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
