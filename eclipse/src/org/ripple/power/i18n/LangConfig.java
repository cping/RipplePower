package org.ripple.power.i18n;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHConfig;
import org.ripple.power.ui.UIRes;

public class LangConfig {

	private static RHConfig _config;
	
	public static String fontName = "Dialog";

	public static synchronized void init() {
		if (_config == null) {
			try {
				if (Language.CN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					fontName = "宋体";
					_config = new RHConfig(UIRes.getStream("chinese/config"));
				} else {
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("english/config"));
				}
			} catch (Exception ex) {

			}
		}
	}

	public static String get(Object obj, String res, String value) {
		init();
		String clazz = null;
		if (obj instanceof String) {
			clazz = (String) obj;
		} else if (obj instanceof Class) {
			clazz = ((Class<?>) obj).getName();
		} else {
			clazz = obj.getClass().getName();
		}
		String result = _config.getValue(clazz + "." + res);
		if (result == null) {
			result = _config.getValue(res, value);
		}
		return result;
	}

}
