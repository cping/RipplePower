package org.ripple.power.i18n;

import java.util.ResourceBundle;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RHConfig;
import org.ripple.power.ui.UIRes;

public class LangConfig {

	private static RHConfig _config;

	private static I18nSupport _javai18n;

	public static String fontName = "Dialog";
	
	public static void addJavaI18n(ResourceBundle resourceBundle,
			Language language) {
		initJavaI18n();
		_javai18n.addBundle(resourceBundle, language);
	}

	public static void addJavaI18n(String baseName, Language language) {
		initJavaI18n();
		_javai18n.addBundle(baseName, language);
	}

	public static void addJavaI18n(String baseName, Language language,
			String key) {
		initJavaI18n();
		_javai18n.addBundleOnlyIfNeeded(baseName, language, key);
	}

	public String getJavaI18n(String key, Language language) {
		initJavaI18n();
		return _javai18n.translate(key, language);
	}

	public boolean hasJavaI18n(String key, Language language) {
		initJavaI18n();
		return _javai18n.hasKey(key, language);
	}

	public synchronized static void init() {
		if (_config == null) {
			try {
				// 简
				if (Language.SIMPLECN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					fontName = "宋体";
					_config = new RHConfig(UIRes.getStream("cn_zh/mes"));
					// 繁
				} else if (Language.TRADITIONALCN.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("cn_tw/mes"));
					// 日
				} else if (Language.JP.getLocale().equals(
						LSystem.applicationLang.getLocale())) {
					// ＭＳ ゴシック
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("jp/mes"));
					// 其它
				} else {
					fontName = "Dialog";
					_config = new RHConfig(UIRes.getStream("en/mes"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
	}

	private synchronized static void initJavaI18n(){
		if (_javai18n == null) {
			_javai18n = new I18nSupport();
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
			return _config.getValue(res, value);
		}
		if(_javai18n != null&&_javai18n.isDirty()){
				return _javai18n.translate(res, LSystem.applicationLang);
		}
		return result;
	}

}
