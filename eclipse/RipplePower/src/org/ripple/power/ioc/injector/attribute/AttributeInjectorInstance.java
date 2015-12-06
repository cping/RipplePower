package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.reflect.Reflector;

public class AttributeInjectorInstance extends AttributeAbstractInjector {

	private Object instance;

	public AttributeInjectorInstance(Reflector reflector, String attributeName,
			Object instance) {
		super(reflector, attributeName);
		this.instance = instance;
	}

	protected Object getInstance(Container container, Object target) {
		return this.instance;
	}

}
