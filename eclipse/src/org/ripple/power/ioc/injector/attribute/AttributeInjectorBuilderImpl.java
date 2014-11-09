package org.ripple.power.ioc.injector.attribute;

import org.ripple.power.ioc.injector.CompositeInjector;
import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.injector.InjectorFactory;
import org.ripple.power.ioc.reflect.Reflector;

public class AttributeInjectorBuilderImpl implements AttributeInjectorBuilder {

	private CompositeInjector compositeInjector = InjectorFactory.createCompositeInjector();

	public AttributeInjectorBuilder addAttributeKey(Reflector reflector,String attributeName, Object key) {
		this.compositeInjector.addInjector(new AttributeKeyInjector(reflector,attributeName, key));
		return this;
	}


	public AttributeInjectorBuilder addAttributeInstance(Reflector reflector,String attributeName, Object instance) {
		this.compositeInjector.addInjector(new AttributeInjectorInstance(reflector,attributeName, instance));
		return this;
	}


	public AttributeInjectorBuilder addAttribute(Reflector reflector,String attributeName) {
		this.compositeInjector.addInjector(new AutoKeyAttributeInjector(reflector,attributeName));
		return this;
	}

	public void setInjector(Object key, Container container) {
		container.addInjector(key, this.compositeInjector);
	}


}
