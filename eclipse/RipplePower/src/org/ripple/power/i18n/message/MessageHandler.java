package org.ripple.power.i18n.message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

import org.ripple.power.i18n.message.annotations.Message;
import org.ripple.power.i18n.message.annotations.Messages;

public abstract class MessageHandler implements InvocationHandler {

	/**
     * 
     */
	protected final Class<?> proxiedInterface;

	public MessageHandler(Class<?> proxiedInterface) {
		this.proxiedInterface = proxiedInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Messages messageBundle = method.getAnnotation(Messages.class);
		if (messageBundle == null) {
			throw new IllegalStateException("Messages Annotation does not set.");
		}
		Locale locale = Locale.getDefault();
		Message[] messages = messageBundle.value();
		String format = bestFitFormat(locale, messages);
		return format(locale, format, args);
	}

	static String bestFitFormat(Locale locale, Message[] messages) {
		String lang = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		String[] localeList = new String[] { "", lang, lang + "_" + country,
				lang + "_" + country + "_" + variant };

		String[] formats = new String[] { "", "", "", "", "", "" };
		int mostFit = 0;

		for (Message message : messages) {
			String localeStr = message.locale();
			for (int i = mostFit; i < localeList.length; i++) {
				if (localeList[i].equals(localeStr)) {
					mostFit++;
					formats[mostFit] = message.value();
					break;
				}
			}
		}

		return formats[mostFit];
	}

	protected abstract String format(Locale locale, String format, Object[] args);
}
