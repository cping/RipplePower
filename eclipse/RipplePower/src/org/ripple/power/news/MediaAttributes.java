package org.ripple.power.news;

public class MediaAttributes {
	private MediaAttributes() {
	}

	static String stringValue(org.xml.sax.Attributes attributes, String name) {
		return attributes.getValue(name);
	}

	static int intValue(org.xml.sax.Attributes attributes, String name,
			int defaultValue) {
		final String value = stringValue(attributes, name);
		if (value == null) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	static Integer intValue(org.xml.sax.Attributes attributes, String name) {
		final String value = stringValue(attributes, name);
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}
}
