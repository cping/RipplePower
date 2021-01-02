/*
 * Copyright 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * A utility which auto-translates English strings in Android string resources
 * using Google Translate.
 * </p>
 * 
 * <p>
 * Pass the Android client res/ directory as first argument, and optionally
 * message keys who should be forced to retranslate. Usage:
 * {@code StringsResourceTranslator android/res/ [key_1 ...]}
 * </p>
 * 
 * <p>
 * You must set your Google Translate API key into the environment with
 * -DtranslateAPI.key=...
 * </p>
 * 
 * @author Sean Owen
 */
public final class StringsResourceTranslator {

	private static final String API_KEY = System.getProperty("translateAPI.key");
	static {
		if (API_KEY == null) {
			throw new IllegalArgumentException("translateAPI.key is not specified");
		}
	}
	private static final Pattern TRANSLATE_RESPONSE_PATTERN = Pattern.compile("translatedText\":\\s*\"([^\"]+)\"");

	private static final Map<String, String> LANGUAGE_CODE_MASSAGINGS = new HashMap<>(3);
	static {
		LANGUAGE_CODE_MASSAGINGS.put("zh-rCN", "zh-cn");
		LANGUAGE_CODE_MASSAGINGS.put("zh-rTW", "zh-tw");
	}

	private StringsResourceTranslator() {
	}


	static String translateString(String english, String language) throws IOException {
		if ("en".equals(language)) {
			return english;
		}
		String massagedLanguage = LANGUAGE_CODE_MASSAGINGS.get(language);
		if (massagedLanguage != null) {
			language = massagedLanguage;
		}
		System.out.println("  Need translation for " + english);

		URI translateURI = URI.create("https://www.googleapis.com/language/translate/v2?key=" + API_KEY + "&q="
				+ URLEncoder.encode(english, "UTF-8") + "&source=en&target=" + language);
		CharSequence translateResult = fetch(translateURI);
		Matcher m = TRANSLATE_RESPONSE_PATTERN.matcher(translateResult);
		if (!m.find()) {
			System.err.println("No translate result");
			System.err.println(translateResult);
			return english;
		}
		String translation = m.group(1);

		// This is a little crude; unescape some common escapes in the raw
		// response
		translation = translation.replaceAll("&quot;", "\"");
		translation = translation.replaceAll("&#39;", "'");
		translation = translation.replaceAll("&amp;quot;", "\"");
		translation = translation.replaceAll("&amp;#39;", "'");

		System.out.println("  Got translation " + translation);
		return translation;
	}

	private static CharSequence fetch(URI translateURI) throws IOException {
		URLConnection connection = translateURI.toURL().openConnection();
		connection.connect();
		StringBuilder translateResult = new StringBuilder(200);
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
			char[] buffer = new char[8192];
			int charsRead;
			while ((charsRead = in.read(buffer)) > 0) {
				translateResult.append(buffer, 0, charsRead);
			}
		}
		return translateResult;
	}

}
