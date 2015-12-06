package org.ripple.power.news;

import java.net.URI;

public abstract class RssElement {
	void setTitle(String title) {
	}

	void setDescription(String description) {
	}

	void setContent(String content) {
	}

	void setLink(URI link) {
	}

	void setPubDate(String pubDate) {
	}

	void addCategory(String category) {
	}

	void setLastBuildDate(String lastBuildDate) {
	}

	void setTtl(String ttl) {
	}

	void setMediaEnclosure(MediaEnclosure mediaEnclosure) {
	}

	void addMediaThumbnail(MediaThumbnail mediaThumbnail) {
	}

	void setSubtitle(String subtitle) {
	}

	void setDuration(String subtitle) {
	}
}