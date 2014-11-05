package org.ripple.power.i18n.message.handlers;

import java.util.Locale;

import org.ripple.power.i18n.message.MessageHandler;


public class StringFormatMessageHandler extends MessageHandler {

    public StringFormatMessageHandler(Class<?> proxiedInterface) {
        super(proxiedInterface);
    }

    @Override
    protected String format(Locale locale, String format, Object[] args) {
        return String.format(locale, format, args);
    }

}
