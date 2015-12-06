package org.ripple.power.ioc.injector;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;

import org.ripple.power.ioc.reflect.Reflector;
import org.ripple.power.utils.CollectionUtils;
import org.ripple.power.utils.ReflectorUtils;

public class ClassBindImpl implements ClassBind {

	private Class<?> classDependency;
	private Collection<Object> constructorParameters = CollectionUtils
			.createCollection();

	public ClassBindImpl(Class<?> classDependency) {
		this.classDependency = classDependency;
	}

	private void fillConstructorParameters() {
		if (this.constructorParameters.isEmpty()) {
			Collection<Object> constructors = CollectionUtils
					.createCollection(classDependency.getDeclaredConstructors());
			Constructor<?> constructor = (Constructor<?>) CollectionUtils
					.first(constructors);
			Collection<Object> collection = CollectionUtils
					.createCollection(constructor.getParameterTypes());
			CollectionUtils.visitor(collection, new Dispose() {
				public void accept(Object object) {
					ClassBindImpl.this.addKeyParam(object);
				}

				public void accept() {
				}
			});
		}
	}

	public Object instance(Container container) {

		fillConstructorParameters();
		Collection<Object> instances = CollectionUtils.createCollection();
		Object obj = null;
		if (constructorParameters.size() == 0) {
			obj = Reflector.getReflector(classDependency).newInstance();
		} else {
			for (Iterator<Object> it = this.constructorParameters.iterator(); it
					.hasNext();) {
				Bind dependency = (Bind) it.next();
				instances.add(dependency.instance(container));
			}
			obj = ReflectorUtils.invokeContructor(classDependency, instances);
		}
		return obj;
	}

	public ClassBind addInstanceParam(Object instance) {
		addConstructorParameter(InjectorFactory
				.createInstanceDependency(instance));
		return this;
	}

	public ClassBind addKeyParam(Object key) {
		addConstructorParameter(new BindDelegated(key));
		return this;
	}

	private void addConstructorParameter(Bind dependency) {
		this.constructorParameters.add(dependency);
	}

}
