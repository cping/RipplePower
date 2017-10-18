package org.ripple.power.i18n;

import java.awt.ComponentOrientation;
import java.util.Locale;
import java.util.ResourceBundle;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.RPConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.StringUtils;

import com.google.common.base.Preconditions;

public class LangConfig {

	private static RPConfig _config;

	private static I18nSupport _javai18n;

	private static String fontName = "Dialog";

	public static String getFontName() {
		init();
		return fontName;
	}

	public static ComponentOrientation currentComponentOrientation() {
		return ComponentOrientation.getOrientation(Language.DEF.getLocale());
	}

	public static boolean isLeftToRight() {
		return ComponentOrientation.getOrientation(Language.DEF.getLocale()).isLeftToRight();
	}

	public static boolean isEast() {
		return isEastLocale(Language.DEF.getLocale());
	}

	public static Locale newLocaleFromCode(String value) {
		Preconditions.checkNotNull(value, "'value' must be present");
		String[] parameters = value.split("_");
		Preconditions.checkState(parameters.length > 0, "'value' must not be empty");
		final Locale newLocale;
		switch (parameters.length) {
		case 1:
			newLocale = new Locale(parameters[0]);
			break;
		case 2:
			newLocale = new Locale(parameters[0], parameters[1]);
			break;
		case 3:
			newLocale = new Locale(parameters[0], parameters[1], parameters[2]);
			break;
		default:
			throw new IllegalArgumentException("Unknown locale descriptor: " + value);
		}
		return newLocale;
	}

	public static boolean isEastLocale(Locale locale) {
		return locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE) || locale.equals(new Locale("zh", "HK"))
				|| locale.equals(Locale.TAIWAN) || locale.equals(Locale.JAPAN) || locale.equals(Locale.JAPANESE)
				|| locale.equals(Locale.KOREA) || locale.equals(Locale.KOREAN);
	}

	public static void addJavaI18n(ResourceBundle resourceBundle, Language language) {
		initJavaI18n();
		_javai18n.addBundle(resourceBundle, language);
	}

	public static void addJavaI18n(String baseName, Language language) {
		initJavaI18n();
		_javai18n.addBundle(baseName, language);
	}

	public static void addJavaI18n(String baseName, Language language, String key) {
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
				if (Language.SIMPLECN.getLocale().equals(LSystem.applicationLang.getLocale())) {
					fontName = "宋体";
					_config = new RPConfig(UIRes.getStream("cn_zh/mes"));
					// 繁
				} else if (Language.TRADITIONALCN.getLocale().equals(LSystem.applicationLang.getLocale())) {
					fontName = "Dialog";
					_config = new RPConfig(UIRes.getStream("cn_tw/mes"));
					// 日
				} else if (Language.JP.getLocale().equals(LSystem.applicationLang.getLocale())) {
					// ＭＳ ゴシック
					fontName = "Dialog";
					_config = new RPConfig(UIRes.getStream("jp/mes"));
					// 其它
				} else {
					fontName = "Dialog";
					_config = new RPConfig(UIRes.getStream("en/mes"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}
	}

	private static void initJavaI18n() {
		if (_javai18n == null) {
			_javai18n = new I18nSupport();
		}
	}

	public static String get(Object obj, String res, String value) {
		init();
		String result = null;
		if (obj == null) {
			result = _config.getValue(res, value);
		}
		if (result == null) {
			String clazz = null;
			if (obj instanceof String) {
				clazz = (String) obj;
			} else if (obj instanceof Class) {
				clazz = ((Class<?>) obj).getName();
			} else {
				clazz = obj.getClass().getName();
			}
			result = _config.getValue(clazz + "." + res);
			if (result == null) {
				result = _config.getValue(res, value);
			}
			if (result == null && _javai18n != null && _javai18n.isDirty()) {
				result = _javai18n.translate(res, LSystem.applicationLang);
			}
		}
		if (result != null && result.indexOf("\\n") != -1) {
			result = StringUtils.replace(result, "\\n", "\n");
		}
		if (result != null && result.indexOf("\\r") != -1) {
			result = StringUtils.replace(result, "\\r", "\r");
		}
		return result;
	}

}
