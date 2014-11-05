package org.ripple.power.news;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NewsParser {

	public static class News {

		public String title;
		public String url;

		public News(String t, String u) {
			this.title = t.replace("&quot;", "");
			this.url = u;
		}

		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (!(o instanceof News)) {
				return false;
			}
			if (o == this) {
				return true;
			}
			if (url.equals(((News) o).url)) {
				return true;
			}
			return false;
		}
	}

	public synchronized static List<News> getAllNew(String query) {
		ArrayList<News> list = new ArrayList<News>(100);
		List<RssItem> baidu = null;
		List<RssItem> google = null;
		List<Feedzilla> feedzilla = null;
		try {
			feedzilla = getFeedzillaRssItem(query, -1);
		} catch (Exception e) {
		}
		try {
			google = getGoogleRssItem(query);
		} catch (Exception e) {
		}
		try {
			baidu = getBaiduRssItem(query);
		} catch (Exception e) {
		}
		if (feedzilla != null) {
			for (Feedzilla feed : feedzilla) {
				list.add(new News(feed.title, feed.url));
			}
		}
		try {
			feedzilla = getFeedzillaRssItem(query, 0);
		} catch (Exception e) {
		}
		if (feedzilla != null) {
			for (Feedzilla feed : feedzilla) {
				list.add(new News(feed.title, feed.url));
			}
		}
		if (google != null) {
			for (RssItem item : google) {
				News news = new News(item.getTitle(), item.getLink().toString());
				if (!list.contains(news)) {
					list.add(news);
				}
			}
		}
		if (baidu != null) {
			for (RssItem item : baidu) {
				News news = new News(item.getTitle(), item.getLink().toString());
				if (!list.contains(news)) {
					list.add(news);
				}
			}
		}
		return list;
	}

	public static List<RssItem> getBaiduRssItem(String query) throws Exception {
		String uri = String
				.format("http://news.baidu.com/ns?word=%s&tn=newsrss&sr=0&cl=1&rn=20&ct=0",
						URLEncoder.encode(query.trim(), "gb2312"));
		HttpRequest request = HttpRequest.get(uri);
		if (request.ok()) {
			RssFeed feed = NewsParser.parse(request.body(), "gb2312");
			return feed.getRssItems();
		}
		return null;
	}

	public static List<RssItem> getGoogleRssItem(String query) throws Exception {
		String uri = String
				.format("http://news.google.co.kr/news?&hl=en&ie=UTF-8&q="
						+ URLEncoder.encode(query, "UTF-8") + "&output=rss");
		HttpRequest request = HttpRequest.get(uri);
		if (request.ok()) {
			RssFeed feed = NewsParser.parse(request.body(), "UTF-8");
			return feed.getRssItems();
		}
		return null;
	}

	public static ArrayList<Feedzilla> getFeedzillaRssItem(String query,
			int offset) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		if (offset < 0) {
			cal.add(Calendar.DATE, offset);
		}
		String yesterday = dateFormat.format(cal.getTime());
		HttpRequest request = HttpRequest
				.get("http://api.feedzilla.com/v1/articles/search.json?q="
						+ URLEncoder.encode(query, "UTF-8")
						+ "&order=date&since=" + yesterday + "&count=50");
		if (request.ok()) {
			String result = request.body();
			JSONObject json = new JSONObject(result);
			JSONArray articles = json.getJSONArray("articles");
			ArrayList<Feedzilla> list = new ArrayList<Feedzilla>(50);
			for (int i = 0; i < articles.length(); i++) {
				JSONObject article = articles.getJSONObject(i);
				String title = article.has("title") ? article
						.getString("title") : null;
				String summary = article.has("summary") ? article
						.getString("summary") : null;
				String source = article.has("source") ? article
						.getString("source") : null;
				String sourceUrl = article.has("source") ? article
						.getString("source_url") : null;
				String published = article.has("publish_date") ? article
						.getString("publish_date") : null;
				String author = null;
				if (article.has("author")) {
					author = article.getString("author");
				}
				String url = article.has("url") ? article.getString("url")
						: null;
				int feedzillaId = 0;
				if (url != null) {
					Pattern p = Pattern.compile("([0-9]+)");
					Matcher m = p.matcher(url);
					if (m.find()) {
						feedzillaId = Integer.parseInt(m.group(1));
					}
				}
				Feedzilla feed = new Feedzilla(title, published, source,
						sourceUrl, url, summary, author, feedzillaId);
				list.add(feed);
			}
			return list;

		}
		return null;
	}

	public static RssFeed parse(byte[] data, String encoding)
			throws IOException, SAXException, ParserConfigurationException {
		return parse(new String(data, encoding), encoding);
	}

	public static RssFeed parse(String data, String encoding)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		RssHandler handler = new RssHandler();
		xmlReader.setContentHandler(handler);
		InputSource source = new InputSource(new ByteArrayInputStream(
				data.getBytes(encoding)));
		xmlReader.parse(source);
		return handler.getRssFeed();
	}
}