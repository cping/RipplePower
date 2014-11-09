package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.reflect.Reflector;
import org.ripple.power.utils.ReflectorUtils;

public class AutoKeyAttributeInjector extends AttributeAbstractInjector {

	public AutoKeyAttributeInjector(Reflector reflector, String attributeName) {
		super(reflector, attributeName);
	}

	private Class inspect(Object target) {
		return ReflectorUtils.getParameterType(target.getClass(),
				this.attributeName, "set");
	}

	protected Object getInstance(Container container, Object target) {
		Class key = inspect(target);
		return container.getInstance(key);
	}

}
