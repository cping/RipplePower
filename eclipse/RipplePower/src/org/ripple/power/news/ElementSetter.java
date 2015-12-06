package org.ripple.power.news;

import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.Attributes;

public enum ElementSetter {
	TITLE("title", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setTitle(value);
		}
	}), DESCRIPTION("description", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setDescription(value);
		}
	}), CONTENT("content:encode", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setContent(value);
		}
	}), LINK("link", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			try {
				element.setLink(new URI(value));
			} catch (URISyntaxException e) {
			}
		}
	}), CATEGORY("category", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.addCategory(value);
		}
	}), PUB_DATE("pubDate", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setPubDate(value);
		}
	}), LAST_BUILD_DATE("lastBuildDate", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setLastBuildDate(value);
		}
	}), TTL("ttl", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setTtl(value);
		}
	}), MEDIA_THUMBNAIL("media:thumbnail", new AttributeSetter() {
		private static final String MEDIA_THUMBNAIL_HEIGHT = "height";
		private static final String MEDIA_THUMBNAIL_WIDTH = "width";
		private static final String MEDIA_THUMBNAIL_URL = "url";
		private static final int DEFAULT_DIMENSION = -1;

		@Override
		public void set(RssElement element, Attributes attributes) {
			final int height = MediaAttributes.intValue(attributes,
					MEDIA_THUMBNAIL_HEIGHT, DEFAULT_DIMENSION);
			final int width = MediaAttributes.intValue(attributes,
					MEDIA_THUMBNAIL_WIDTH, DEFAULT_DIMENSION);
			final String url = MediaAttributes.stringValue(attributes,
					MEDIA_THUMBNAIL_URL);
			if (url == null) {
				return;
			}
			try {
				element.addMediaThumbnail(new MediaThumbnail(new URI(url),
						height, width));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}), ENCLOSURE("enclosure", new AttributeSetter() {
		private static final String URL = "url";
		private static final String LENGTH = "length";
		private static final String MIMETYPE = "type";

		@Override
		public void set(RssElement element, Attributes attributes) {
			final String url = MediaAttributes.stringValue(attributes, URL);
			final Integer length = MediaAttributes.intValue(attributes, LENGTH);
			final String mimeType = MediaAttributes.stringValue(attributes,
					MIMETYPE);
			if (url == null || length == null || mimeType == null) {
				return;
			}
			MediaEnclosure enclosure = null;
			try {
				enclosure = new MediaEnclosure(new URI(url), length, mimeType);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			element.setMediaEnclosure(enclosure);
		}
	}), ITUNES_SUBTITLE("itunes:subtitle", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setSubtitle(value);
		}
	}), ITUNES_DURATION("itunes:duration", new ContentSetter() {
		@Override
		public void set(RssElement element, String value) {
			element.setDuration(value);
		}
	});
	private String mQName;
	private Setter mSetter;

	private ElementSetter(String qName, Setter setter) {
		mQName = qName;
		mSetter = setter;
	}

	private static Setter getSetter(String qName) {
		for (ElementSetter elementSetter : ElementSetter.values()) {
			if (elementSetter.mQName.equals(qName)) {
				return elementSetter.mSetter;
			}
		}
		return null;
	}

	public static void setContent(String qName, RssElement element,
			String content) {
		Setter setter = getSetter(qName);
		if (setter instanceof ContentSetter) {
			((ContentSetter) setter).set(element, content);
		}
	}

	public static void setAttributes(String qName, RssElement element,
			Attributes attributes) {
		Setter setter = getSetter(qName);
		if (setter instanceof AttributeSetter) {
			((AttributeSetter) setter).set(element, attributes);
		}
	}

	public static boolean contains(String qName) {
		return (getSetter(qName) != null);
	}

	public static boolean containsInAttributes(String qName) {
		return (getSetter(qName) instanceof AttributeSetter);
	}

	public static interface Setter {
	}

	public static interface ContentSetter extends Setter {

		void set(RssElement element, String value);
	}

	private static interface AttributeSetter extends Setter {

		void set(RssElement element, Attributes attributes);
	}
}