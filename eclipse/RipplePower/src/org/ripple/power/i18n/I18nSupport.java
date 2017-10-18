package org.ripple.power.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nSupport {

	private HashMap<Language, List<ResourceBundle>> pLanguageToBundles = new HashMap<Language, List<ResourceBundle>>();

	private boolean pDirty;

	public void addBundle(ResourceBundle resourceBundle, Language language) {
		List<ResourceBundle> bundles = pLanguageToBundles.get(language);
		if (bundles == null) {
			bundles = new ArrayList<ResourceBundle>();
			pLanguageToBundles.put(language, bundles);
		}
		bundles.add(resourceBundle);
		pDirty = true;
	}

	public void addBundle(String baseName, Language language) {
		Locale oldLocale = Locale.getDefault();
		Locale.setDefault(language.getLocale());
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, language.getLocale());
		addBundle(bundle, language);
		Locale.setDefault(oldLocale);
		pDirty = true;
	}

	public void addBundleOnlyIfNeeded(String baseName, Language language, String key) {
		try {
			translate(key, language);
		} catch (IllegalArgumentException e) {
			addBundle(baseName, language);
		} catch (MissingResourceException e) {
			addBundle(baseName, language);
		}
	}

	public String translate(String key, Language language) {
		List<ResourceBundle> bundles = pLanguageToBundles.get(language);
		if (bundles == null) {
			throw new IllegalArgumentException("Can't find bundle for '" + language + "' language.");
		}
		MissingResourceException exception = null;
		for (ResourceBundle bundle : bundles) {
			try {
				return bundle.getString(key);
			} catch (MissingResourceException e) {
				exception = e;
			}
		}
		throw exception;
	}

	public boolean hasKey(String key, Language language) {
		try {
			translate(key, language);
		} catch (MissingResourceException e) {
			return false;
		}
		return true;
	}

	public boolean isDirty() {
		return pDirty;
	}

}
