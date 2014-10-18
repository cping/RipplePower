package org.ripple.power.i18n;

import java.io.IOException;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHConfig;
import org.ripple.power.ui.UIRes;

public class LangConfig {

	private static RHConfig _config;

	private static synchronized void init() {
		if (_config == null) {
			try {
				if (Language.CN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					_config = new RHConfig(UIRes.getStream("chinese/config"));
				} else {
					_config = new RHConfig(UIRes.getStream("english/config"));
				}
			} catch (Exception ex) {

			}
		}
	}

	public static String get(Object obj, String res, String value) {
		init();
		return _config.getValue(res, value);
	}

}
