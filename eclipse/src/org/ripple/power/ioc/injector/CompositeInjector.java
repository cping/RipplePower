package org.ripple.power.ioc.injector;

import java.util.Collection;
import java.util.Iterator;

import org.ripple.power.utils.CollectionUtils;

public class CompositeInjector implements Injector {

	private Collection<Object> injectors = CollectionUtils.createCollection();

	public void inject(Container container, Object target) {
		for (Iterator<Object> it = injectors.iterator(); it.hasNext();) {
			Injector injector = (Injector) it.next();

			injector.inject(container, target);
		}
	}

	public Collection<Object> injects() {
		return injectors;
	}

	public CompositeInjector addInjector(Injector injector) {
		injectors.add(injector);
		return this;
	}

}
