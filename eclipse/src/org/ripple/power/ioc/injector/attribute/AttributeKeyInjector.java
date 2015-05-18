package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.reflect.Reflector;

public class AttributeKeyInjector extends AttributeAbstractInjector {

	private Object key;

	public AttributeKeyInjector(Reflector reflector, String attributeName,
			Object key) {
		super(reflector, attributeName);
		this.key = key;
	}

	protected Object getInstance(Container container, Object target) {
		return container.getInstance(key);
	}

}
