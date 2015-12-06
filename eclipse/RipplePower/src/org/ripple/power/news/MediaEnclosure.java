package org.ripple.power.news;

import java.net.URI;

public class MediaEnclosure {
	private final URI url;
	private final int length;
	private final String mimeType;

	public URI getUrl() {
		return url;
	}

	public int getLength() {
		return length;
	}

	public String getMimeType() {
		return mimeType;
	}

	MediaEnclosure(URI url, int length, String mimeType) {
		this.url = url;
		this.length = length;
		this.mimeType = mimeType;
	}
}
