package org.ripple.power.news;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Feedzilla {
	public int feedzillaId;
	public String title;
	public Date published;
	public String source;
	public String sourceUrl;
	public String url;
	public String summary;
	public String author;
	public long stamp;

	public Feedzilla(String title, String published, String source,
			String sourceUrl, String url, String summary, String author,
			int feedzillaId) throws ParseException {
		this.title = title;
		this.published = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss +SSSS", Locale.ENGLISH)
				.parse(published);
		this.stamp = this.published.getTime();
		this.source = source;
		this.sourceUrl = sourceUrl;
		this.url = url;
		this.summary = summary;
		this.author = author;
		this.feedzillaId = feedzillaId;
	}
}
