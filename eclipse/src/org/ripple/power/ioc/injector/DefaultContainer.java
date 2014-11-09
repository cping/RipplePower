package org.ripple.power.ioc.injector;

import org.ripple.power.ioc.injector.attribute.AbstractContainer;

public class DefaultContainer extends AbstractContainer {
	
	public DefaultContainer() {
		super(new DefaultComponentFactory());
	}
	
}
