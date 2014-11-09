package org.ripple.power.ioc.injector;

import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilder;

public interface ComponentFactory {

	public AttributeInjectorBuilder createAttributeInjectorBuilder();

	public ClassBind createClassBind(Class classDependency);

	public Bind createInstanceBind(Object instance);

	public BindMediator createBindMediator(Bind dependency, Container container);

}
