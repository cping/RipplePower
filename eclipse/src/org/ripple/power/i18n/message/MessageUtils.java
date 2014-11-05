package org.ripple.power.i18n.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import org.ripple.power.i18n.message.annotations.Handler;
import org.ripple.power.i18n.message.handlers.MessageFormatMessageHandler;
import org.ripple.power.i18n.message.handlers.StringFormatMessageHandler;


public class MessageUtils {

    public static <T> T createMessages(Class<T> clazz) {
        Handler annotation = clazz.getAnnotation(Handler.class);
        Class<? extends MessageHandler> handlerClass;
        if (annotation == null) {
            return createMessagesByStringFormat(clazz);
        } else {
            handlerClass = annotation.value();
        }
        return createMessages(clazz, handlerClass);
    }

    public static <T> T createMessages(Class<T> clazz,
            Class<? extends MessageHandler> handlerClass) {
        try {
            MessageHandler handler = handlerClass.getConstructor(Class.class)
                    .newInstance(clazz);
            return createMessages(clazz, handler);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T createMessages(Class<T> clazz, MessageHandler handler) {
        Object obj = Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[] { clazz }, handler);
        return clazz.cast(obj);
    }

    public static <T> T createMessagesByStringFormat(Class<T> clazz) {
        return createMessages(clazz, new StringFormatMessageHandler(clazz));
    }

    public static <T> T createMessagesByMessageFormat(Class<T> clazz) {
        return createMessages(clazz, new MessageFormatMessageHandler(clazz));
    }
}
