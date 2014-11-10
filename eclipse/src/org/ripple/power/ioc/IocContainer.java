package org.ripple.power.ioc;

import java.util.HashSet;
import java.util.Set;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.injector.InjectorFactory;

public class IocContainer {

	final Container defaultContainer;

	final Set<Object> args;

	public IocContainer() {
		defaultContainer = InjectorFactory.createContainer();
		args = new HashSet<Object>(20);
	}

	public Object getInstance(Class<?> clazz) {
		return defaultContainer.getInstance(clazz);
	}

	public void addValue(Object value) {
		args.add(value);
	}

	public void removeValues() {
		args.clear();
	}

	public void addConstructor(Class<?> clazz) {
		IocFactory.bind(defaultContainer, clazz, args.toArray());
	}

	public void addConstructor(Class<?> clazz, Object[] args) {
		IocFactory.bind(defaultContainer, clazz, args);
	}

	public Container getContainer() {
		return defaultContainer;
	}

	public void initialize() {
		defaultContainer.start();
	}

	public void destroy() {
		defaultContainer.stop();
		args.clear();
	}

	public void finalize() {
		destroy();
	}

}
