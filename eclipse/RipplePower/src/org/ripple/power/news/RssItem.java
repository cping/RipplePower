package org.ripple.power.news;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RssItem extends RssElement {
	private String title;
	private String description;
	private String content;
	private URI link;
	private String pubDate;
	private List<String> categories;
	private MediaEnclosure mediaEnclosure;
	private List<MediaThumbnail> mediaThumbnails;
	private String subtitle;
	private String duration;

	void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	void setLink(URI link) {
		this.link = link;
	}

	public URI getLink() {
		return link;
	}

	void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getPubDate() {
		return pubDate;
	}

	void addCategory(String category) {
		if (categories == null) {
			categories = new ArrayList<String>();
		}
		categories.add(category);
	}

	public List<String> getCategories() {
		return categories;
	}

	void setMediaEnclosure(MediaEnclosure mediaEnclosure) {
		this.mediaEnclosure = mediaEnclosure;
	}

	public MediaEnclosure getMediaEnclosure() {
		return mediaEnclosure;
	}

	void addMediaThumbnail(MediaThumbnail mediaThumbnail) {
		if (mediaThumbnails == null) {
			mediaThumbnails = new ArrayList<MediaThumbnail>();
		}
		mediaThumbnails.add(mediaThumbnail);
	}

	public List<MediaThumbnail> getMediaThumbnails() {
		return mediaThumbnails;
	}

	void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getSubtitle() {
		return subtitle;
	}

	void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}
}