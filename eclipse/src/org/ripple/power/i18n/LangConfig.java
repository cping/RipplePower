package org.ripple.power.i18n;

import java.util.Locale;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHConfig;
import org.ripple.power.ui.UIRes;

public class LangConfig {

	private static RHConfig _config;

	public static String fontName = "Dialog";

	public static synchronized void init() {
		if (_config == null) {
			try {
				//简
				if (Language.SIMPLECN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					fontName = "宋体";
					_config = new RHConfig(UIRes.getStream("cn_zh/mes"));
				//繁	
				} else if (Language.TRADITIONALCN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("cn_tw/mes"));
				//日	
				} else if (Language.JP.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					// ＭＳ ゴシック
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("jp/mes"));
				//其它	
				} else {
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("en/mes"));
				}
			} catch (Exception ex) {

			}
		}
	}

	public static String get(Object obj, String res, String value) {
		init();
		if (obj == null) {
			return _config.getValue(res, value);
		}
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

	public static void main(String[] args) {
		System.out.println(Locale.CHINA);
	}
}
