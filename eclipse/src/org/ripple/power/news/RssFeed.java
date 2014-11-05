package org.ripple.power.news;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RssFeed extends RssElement {
	private String title;
	private String description;
	private URI link;
	private String pubDate;
	private String lastBuildDate;
	private int ttl;
	private List<String> categories;
	private List<RssItem> rssItems;
	private String subtitle;

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

	void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	public String getLastBuildDate() {
		return lastBuildDate;
	}

	void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getTtl() {
		return ttl;
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

	void addRssItem(RssItem rssItem) {
		if (rssItems == null) {
			rssItems = new ArrayList<RssItem>();
		}
		rssItems.add(rssItem);
	}

	public List<RssItem> getRssItems() {
		return rssItems;
	}

	void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getSubtitle() {
		return subtitle;
	}
}