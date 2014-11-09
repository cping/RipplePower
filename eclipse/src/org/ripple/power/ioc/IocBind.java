package org.ripple.power.ioc;

import java.util.Set;

import org.ripple.power.ioc.injector.Container;
import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilder;
import org.ripple.power.ioc.reflect.Reflector;
import org.ripple.power.utils.ReflectorUtils;

final public class IocBind implements Ioc {

	private Reflector reflector;

	private Container container;

	private Object object;

	private AttributeInjectorBuilder builder;

	private int model = 0;

	IocBind(final Container container, final Class clazz) {
		this(container, clazz, 0);
	}

	IocBind(final Container container, final Class clazz, final int model) {
		this(container, clazz, null, 0);
	}

	IocBind(final Container container, final Class clazz, final Object[] args) {
		this(container, clazz, args, 0);
	}

	IocBind(final Container container, final Class clazz, final Object[] args,
			final int model) {
		this.container = container;
		this.reflector = Reflector.getReflector(clazz);
		if (args == null) {
			object = reflector.newInstance();
		} else {
			object = reflector.newInstance(args);
		}
		this.model = model;
		this.container.addInstanceBind(clazz, object);
		this.builder = container.addAttributeInjector(clazz);
	}

	IocBind(final Container container, final Object source, final int model) {
		if (container == null) {
			throw new RuntimeException("!");
		}
		if (source == null) {
			throw new RuntimeException("!");
		}
		Class clazz = source.getClass();
		this.container = container;
		this.reflector = Reflector.getReflector(clazz);
		this.object = source;
		this.model = model;
		this.container.addInstanceBind(clazz, object);
		this.builder = container.addAttributeInjector(clazz);
	}

	private void bindIoc() {
		object = getObject();
	}

	public Object getThis() {
		return object;
	}

	public Ioc getChild(String method) {
		Ioc ioc = null;
		try {
			ioc = new IocBind(container, doInvoke(method), model);
		} catch (Exception e) {
		}
		return ioc;
	}

	public Set getFeilds() {
		return reflector.getFields();
	}

	public Object getFeild(final String name) {
		bindIoc();
		try {
			return ReflectorUtils.getField(object, name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isImplInterface(final Class clazz) {
		return reflector.isImplInterface(clazz);
	}

	public boolean isImplInterface(final String className) {
		return reflector.isImplInterface(className);
	}

	public Object doInvoke(final String methodName, final Object[] args)
			throws Exception {
		bindIoc();
		return reflector.doInvoke(object, methodName, args);
	}

	public Object doInvoke(String methodName) throws Exception {
		bindIoc();
		return reflector.doInvoke(object, ReflectorUtils.getMatchGetMethod(
				reflector.getReflectedClass(), methodName), null);
	}

	public Container getContainer() {
		return container;
	}

	public void setMethod(String attributeName, String value) {
		setMethod(attributeName, value);
	}

	public void setMethod(String attributeName, byte value) {
		setMethod(attributeName, new Byte(value));
	}

	public void setMethod(String attributeName, char value) {
		setMethod(attributeName, new Character(value));
	}

	public void setMethod(String attributeName, short value) {
		setMethod(attributeName, new Short(value));
	}

	public void setMethod(String attributeName, int value) {
		setMethod(attributeName, new Integer(value));
	}

	public void setMethod(String attributeName, long value) {
		setMethod(attributeName, new Long(value));
	}

	public void setMethod(String attributeName, double value) {
		setMethod(attributeName, new Double(value));
	}

	public void setMethod(String attributeName, float value) {
		setMethod(attributeName, new Float(value));
	}

	public void setMethod(String attributeName, boolean value) {
		setMethod(attributeName, new Boolean(value));
	}

	public void setMethod(final String attributeName, final Object value) {
		switchMethod(attributeName, value);
	}

	private void switchMethod(final String methodName, final Object value) {
		switch (model) {
		case MODEL.LIKE:
			builder.addAttributeInstance(reflector, methodName, value);
			break;
		case MODEL.GUESS:
			container.addAttributeInjector(reflector.getReflectedClass())
					.addAttributeInstance(reflector, methodName, value);
			break;
		default:
			break;
		}
		return;
	}

	public Object getObject() {
		switch (model) {
		case MODEL.LIKE:
			return container.getInstance(reflector.getReflectedClass());
		case MODEL.GUESS:
			container.inject(object);
			return object;
		default:
			return null;
		}
	}

	private interface MODEL {
		int LIKE = 0;

		int GUESS = 1;
	}

	public void finalize() {
		reflector = null;
		builder = null;
		object = null;
	}
}
