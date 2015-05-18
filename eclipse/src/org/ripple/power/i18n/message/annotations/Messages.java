package org.ripple.power.i18n.message.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;

@Retention(RUNTIME)
@Target({ FIELD, METHOD, TYPE })
public @interface Messages {
	Message[] value();
}
