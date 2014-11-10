package org.ripple.power.ioc;

import java.util.Map;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.injector.InjectorFactory;

public class IocFactory {

	final static Container defaultContainer = InjectorFactory.createContainer();

	public static Container getDefaultContainer() {
		return defaultContainer;
	}

	public static void initialize() {
		defaultContainer.start();
	}

	public static void destroy() {
		defaultContainer.stop();
	}

	public static Ioc bind(final Container container, final Class<?> clazz) {
		return new IocBind(container, clazz);
	}

	public static Ioc bind(final Container container, final Class<?> clazz,
			final int model) {
		return new IocBind(container, clazz, model);
	}

	public static Ioc bind(final Container container, final String className) {
		try {
			return new IocBind(container, Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage() + " Unable to load!");
		}
	}

	public static Ioc bind(final Container container, final String className,
			final Map<Object, Object> args) {
		return bind(container, className, args.values().toArray());
	}

	public static Ioc bind(final Container container, final String className,
			final Object[] args) {
		try {
			return new IocBind(container, Class.forName(className), args);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage() + " Unable to load!");
		}
	}

	public static Ioc bind(final Container container, final Class<?> clazz,
			final Object[] args) {
		return new IocBind(container, clazz, args);
	}

	public static Ioc bind(final Container container, final Class<?> clazz,
			final Object[] args, final int model) {
		return new IocBind(container, clazz, args, model);
	}

	// ---------- 使用默认容器--------------//
	public static Ioc bind(final Class<?> clazz) {
		return new IocBind(defaultContainer, clazz);
	}

	public static Ioc bind(final Class<?> clazz, final int model) {
		return new IocBind(defaultContainer, clazz, model);
	}

	public static Ioc bind(final String className) {
		try {
			return new IocBind(defaultContainer, Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage() + " Unable to load!");
		}
	}

	public static Ioc bind(final String className, final Map<Object,Object> args) {
		return bind(defaultContainer, className, args.values().toArray());
	}

	public static Ioc bind(final String className, final Object[] args) {
		try {
			return new IocBind(defaultContainer, Class.forName(className), args);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage() + " Unable to load!");
		}
	}

	public static Ioc bind(final Class<?> clazz, final Object[] args) {
		return new IocBind(defaultContainer, clazz, args);
	}

	public static Ioc bind(final Class<?> clazz, final Object[] args,
			final int model) {
		return new IocBind(defaultContainer, clazz, args, model);
	}
}
