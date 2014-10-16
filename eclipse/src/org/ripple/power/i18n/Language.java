package org.ripple.power.i18n;

import java.util.Locale;

public enum Language {

	CN(Locale.CHINESE), EN(Locale.ENGLISH), JP(Locale.JAPANESE), DEF();

	private Locale locale;

	Language(Locale locale) {
		this.locale = locale;
	}

	Language() {
		this(Locale.getDefault());
	}

	public Locale getLocale() {
		return locale;
	}
}