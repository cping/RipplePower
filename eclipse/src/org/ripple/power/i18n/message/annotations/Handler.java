package org.ripple.power.i18n.message.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ripple.power.i18n.message.handlers.StringFormatMessageHandler;
import org.ripple.power.i18nl.message.MessageHandler;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Handler {
    Class<? extends MessageHandler> value() default StringFormatMessageHandler.class;
}
