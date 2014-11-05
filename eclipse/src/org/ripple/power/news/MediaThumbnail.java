package org.ripple.power.news;

import java.net.URI;

public class MediaThumbnail {
	private final URI url;
	private final int height;
	private final int width;

	public URI getUrl() {
		return url;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	MediaThumbnail(URI url, int height, int width) {
		this.url = url;
		this.height = height;
		this.width = width;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof MediaThumbnail) {
			final MediaThumbnail other = (MediaThumbnail) (object);
			return url.equals(other.url);
		} else {
			return false;
		}
	}
}
