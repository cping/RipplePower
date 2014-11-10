package org.ripple.power.ioc.injector;

import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilder;
import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilderImpl;

public class InjectorFactory {

	protected InjectorFactory() {
		super();
	}

	public static Container createContainer() {
		return new DefaultContainer();
	}

	public static ClassBindImpl createClassDependency(Class<?> classDependency) {
		return new ClassBindImpl(classDependency);
	}
	
	public static Bind createInstanceDependency(Object instance) {
		return new BindInstance(instance);
	}

	public static AttributeInjectorBuilder createAttributeInjectorBuilder() {
		return new AttributeInjectorBuilderImpl();
	}
	
	public static CompositeInjector createCompositeInjector() {
		return new CompositeInjector();
	}

}
