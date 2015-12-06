package org.ripple.power.news;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class RssHandler extends DefaultHandler {

	private static final String RSS_ITEM = "item";

	private final RssFeed mRssFeed = new RssFeed();

	private RssItem mRssItem;

	private StringBuilder mStringBuilder;
	private boolean mHasSetter;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		mHasSetter = ElementSetter.contains(qName);
		if (!mHasSetter) {
			if (RSS_ITEM.equals(qName)) {
				mRssItem = new RssItem();
			}
		} else if (ElementSetter.containsInAttributes(qName)) {
			ElementSetter.setAttributes(qName, (mRssItem == null ? mRssFeed
					: mRssItem), attributes);
		} else {
			mStringBuilder = new StringBuilder();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (isBuffering()) {
			ElementSetter.setContent(qName, (mRssItem == null ? mRssFeed
					: mRssItem), mStringBuilder.toString());
			mStringBuilder = null;
		} else if (RSS_ITEM.equals(qName)) {
			mRssFeed.addRssItem(mRssItem);
			mRssItem = null;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if (isBuffering()) {
			mStringBuilder.append(ch, start, length);
		}
	}

	boolean isBuffering() {
		return mStringBuilder != null && mHasSetter;
	}

	RssFeed getRssFeed() {
		return mRssFeed;
	}
}