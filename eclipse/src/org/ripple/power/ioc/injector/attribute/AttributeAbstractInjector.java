package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.reflect.Reflector;

public abstract class AttributeAbstractInjector implements AttributeInjector {

	protected String attributeName;

	private Reflector reflector;

	public AttributeAbstractInjector(Reflector reflector, String attributeName) {
		this.attributeName = attributeName;
		this.reflector = reflector;
	}

	final public void inject(Container container, Object target) {
		Object result = getInstance(container, target);
		if (!(result instanceof Object[])) {
			result = new Object[] { result };
		}
		reflector.setInvoke(target, attributeName, (Object[]) result);
	}

	protected abstract Object getInstance(Container container, Object target);

}
