package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.reflect.Reflector;


public interface AttributeInjectorBuilder {

	public AttributeInjectorBuilder addAttributeKey(Reflector reflector,String attributeName, Object key);

	public AttributeInjectorBuilder addAttributeInstance(Reflector reflector,String attributeName, Object instance);
	
	public AttributeInjectorBuilder addAttribute(Reflector reflector,String attributeName);

	public void setInjector(Object key, Container container);

}