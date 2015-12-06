package org.ripple.power.config;

public class ApplicationVersion {

	private final String version;

	private final String build;

	public ApplicationVersion(String version, String build) {
		super();
		this.version = version;
		this.build = build;
	}

	public boolean isNewerThan(String anotherVersion) {
		if (anotherVersion != null && build != null) {
			return (anotherVersion.compareTo(build) < 0);
		}
		return false;
	}

	public String getVersion() {
		return version;
	}

	public String getBuild() {
		return build;
	}
}
