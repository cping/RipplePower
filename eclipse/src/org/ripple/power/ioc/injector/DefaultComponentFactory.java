package org.ripple.power.ioc.injector;

import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilder;

public class DefaultComponentFactory implements ComponentFactory {


	public AttributeInjectorBuilder createAttributeInjectorBuilder() {
		return InjectorFactory.createAttributeInjectorBuilder();
	}

	public ClassBind createClassBind(Class classDependency) {
		return InjectorFactory.createClassDependency(classDependency);
	}

	
	public Bind createInstanceBind(Object instance) {
		return InjectorFactory.createInstanceDependency(instance);
	}


	public BindMediator createBindMediator(Bind dependency, Container container) {
		return new BindMediator(dependency, container);
	}

}
