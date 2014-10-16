package org.ripple.power.i18n.message.handlers;

import java.text.MessageFormat;
import java.util.Locale;

import org.ripple.power.i18nl.message.MessageHandler;


public class MessageFormatMessageHandler extends MessageHandler {

    public MessageFormatMessageHandler(Class<?> proxiedInterface) {
        super(proxiedInterface);
    }

    @Override
    protected String format(Locale locale, String format, Object[] args) {
        return MessageFormat.format(format, args);
    }

}
