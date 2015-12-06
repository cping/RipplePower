package org.ripple.power.ioc.injector.attribute;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ripple.power.ioc.injector.Bind;
import org.ripple.power.ioc.injector.BindInterceptor;
import org.ripple.power.ioc.injector.BindMediator;
import org.ripple.power.ioc.injector.ClassBind;
import org.ripple.power.ioc.injector.ClassBindImpl;
import org.ripple.power.ioc.injector.ComponentFactory;
import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.injector.Injector;
import org.ripple.power.ioc.injector.Interceptor;
import org.ripple.power.ioc.injector.Start;
import org.ripple.power.utils.CollectionUtils;

public abstract class AbstractContainer implements Container {

	private Map<Object, Object> injectors = CollectionUtils.createMap();

	private Map<Object, Object> binds = CollectionUtils.createMap();

	private Interceptor interceptor = this.createInterceptor();

	private boolean started;

	private ComponentFactory factory;

	public AbstractContainer(ComponentFactory factory) {
		this.factory = factory;
	}

	public void inject(Object key, Object target) {
		Injector injector = this.getInjector(key);

		if (injector != null && target != null) {

			injector.inject(this, target);
		}
	}

	private Injector getInjector(Object key) {
		return (Injector) this.injectors.get(key);
	}

	public Container addInjector(Object key, Injector injector) {
		this.injectors.put(key, injector);
		return this;
	}

	private Object getKey(Object key) {

		Object rightKey = key;
		Collection<Object> keys = CollectionUtils.createList();

		if (!binds.containsKey(key) && key instanceof Class) {
			for (Iterator<Object> it = binds.keySet().iterator(); it.hasNext();) {
				Object candidateKey = (Object) it.next();

				if (candidateKey instanceof Class) {
					keys.add(((Class<?>) key)
							.isAssignableFrom((Class<?>) candidateKey) ? candidateKey
							: key);
				}
			}
			rightKey = CollectionUtils.first(keys);
		}
		return rightKey;
	}

	public Object getInstance(Object key) {
		Object rightKey = getKey(key);
		Object target = null;

		this.interceptor.before(rightKey);
		target = getInstanceFromBind(rightKey);
		this.interceptor.after(rightKey);
		inject(rightKey, target);

		return target;
	}

	public void inject(Object target) {
		for (Iterator<Object> it = this.injectors.keySet().iterator(); it
				.hasNext();) {
			Object key = it.next();
			if (key instanceof Class) {
				Class<?> classKey = (Class<?>) key;

				if (classKey.isAssignableFrom(target.getClass())) {
					Injector injector = getInjector(classKey);
					injector.inject(this, target);
				}
			}
		}
	}

	public Object getAttributeValue(Object key) {
		Object rightKey = key;
		Collection<Object> keys = CollectionUtils.createList();

		if (binds.containsKey(key)) {
			for (Iterator<Object> it = binds.keySet().iterator(); it.hasNext();) {
				Object candidateKey = (Object) it.next();
				keys.add(candidateKey);
			}

			rightKey = CollectionUtils.first(keys);
		}
		return rightKey;
	}

	public Container addBind(Object key, Bind dependency) {
		this.binds.put(key, this.factory.createBindMediator(dependency, this));
		return this;
	}

	private Interceptor createInterceptor() {
		return new BindInterceptor();
	}

	private Object getInstanceFromBind(Object key) {
		BindMediator result = getBindMediator(key);
		if (result != null) {
			return result.getInstance();
		}
		return result;

	}

	private BindMediator getBindMediator(Object key) {
		return (BindMediator) this.binds.get(key);
	}

	public Bind getBind(Object key) {
		BindMediator bindMediator = getBindMediator(key);
		return bindMediator.getBind();
	}

	public Container addInstanceBind(Object key, Object instance) {
		return this.addBind(key, this.factory.createInstanceBind(instance));
	}

	public AttributeInjectorBuilder addAttributeInjector(Object key) {
		AttributeInjectorBuilder attributeInjector = this.factory
				.createAttributeInjectorBuilder();
		attributeInjector.setInjector(key, this);
		return attributeInjector;
	}

	public ClassBind addClassBind(Object key, Class<?> classDependency) {
		ClassBind dependency = new ClassBindImpl(classDependency);
		this.addBind(key, dependency);
		return dependency;
	}

	public void start() {
		if (!this.started) {
			for (Iterator<Object> it = binds.values().iterator(); it.hasNext();) {
				Start startable = (Start) it.next();
				startable.start();
			}
			this.started = true;
		}
	}

	public void stop() {
		if (this.started) {
			for (Iterator<Object> it = binds.values().iterator(); it.hasNext();) {
				Start startable = (Start) it.next();
				startable.stop();
			}
			this.started = false;
		}
	}
}