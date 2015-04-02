package loon.apk.shell;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class APKReflect {

	public static Object invokeStaticMethod(String className,
			String methodName, Class<?>[] paraTypes, Object[] paraValues) {
		try {
			Class<?> objClass = Class.forName(className);
			Method method = objClass.getMethod(methodName, paraTypes);
			method.setAccessible(true);
			return method.invoke(null, paraValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object invokeMethod(String className, String methodName,
			Object obj, Class<?>[] paraTypes, Object[] paraValues) {
		try {
			Class<?> objClass = Class.forName(className);
			Method method = objClass.getMethod(methodName, paraTypes);
			method.setAccessible(true);
			return method.invoke(obj, paraValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getStaticFieldObject(String className, String fieldName) {
		try {
			Class<?> objClass = Class.forName(className);
			Field field = objClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getFieldObject(String className, Object object,
			String fieldName) {
		try {
			Class<?> objClass = Class.forName(className);
			Field field = objClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setFieldOjbect(String className, String fieldName,
			Object obj, Object fieldValue) {
		try {
			Class<?> objClass = Class.forName(className);
			Field field = objClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, fieldValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setStaticOjbect(String className, String fieldName,
			Object fieldValue) {
		try {
			Class<?> objClass = Class.forName(className);
			Field field = objClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(null, fieldValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static APKReflect in(String name) throws Exception {
		return in(forName(name));
	}

	public static APKReflect in(Class<?> clazz) {
		return new APKReflect(clazz);
	}

	public static APKReflect in(Object object) {
		return new APKReflect(object);
	}

	public static <T extends AccessibleObject> T accessible(T accessible) {
		if (accessible == null) {
			return null;
		}

		if (accessible instanceof Member) {
			Member member = (Member) accessible;

			if (Modifier.isPublic(member.getModifiers())
					&& Modifier.isPublic(member.getDeclaringClass()
							.getModifiers())) {

				return accessible;
			}
		}
		if (!accessible.isAccessible()) {
			accessible.setAccessible(true);
		}

		return accessible;
	}

	private final Object object;

	private final boolean isClass;

	private APKReflect(Class<?> type) {
		this.object = type;
		this.isClass = true;
	}

	private APKReflect(Object object) {
		this.object = object;
		this.isClass = false;
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) object;
	}

	public APKReflect set(String name, Object value) throws Exception {
		try {
			Field field = field0(name);
			field.set(object, unwrap(value));
			return this;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public <T> T get(String name) throws Exception {
		return field(name).<T> get();
	}

	public APKReflect field(String name) throws Exception {
		try {
			Field field = field0(name);
			return in(field.get(object));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private Field field0(String name) throws Exception {
		Class<?> type = type();
		try {
			return type.getField(name);
		} catch (NoSuchFieldException e) {
			do {
				try {
					return accessible(type.getDeclaredField(name));
				} catch (NoSuchFieldException ignore) {
				}

				type = type.getSuperclass();
			} while (type != null);

			throw new Exception(e);
		}
	}

	public Map<String, APKReflect> fields() throws Exception {
		Map<String, APKReflect> result = new LinkedHashMap<String, APKReflect>();
		Class<?> type = type();

		do {
			for (Field field : type.getDeclaredFields()) {
				if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
					String name = field.getName();

					if (!result.containsKey(name))
						result.put(name, field(name));
				}
			}

			type = type.getSuperclass();
		} while (type != null);

		return result;
	}

	public APKReflect call(String name) throws Exception {
		return call(name, new Object[0]);
	}

	public APKReflect call(String name, Object... args) throws Exception {
		Class<?>[] types = types(args);
		try {
			Method method = exactMethod(name, types);
			return in(method, object, args);
		} catch (NoSuchMethodException e) {
			try {
				Method method = similarMethod(name, types);
				return in(method, object, args);
			} catch (NoSuchMethodException ex) {
				throw new Exception(ex);
			}
		}
	}

	private Method exactMethod(String name, Class<?>[] types)
			throws NoSuchMethodException {
		Class<?> type = type();
		try {
			return type.getMethod(name, types);
		} catch (NoSuchMethodException e) {
			do {
				try {
					return type.getDeclaredMethod(name, types);
				} catch (NoSuchMethodException ignore) {
				}

				type = type.getSuperclass();
			} while (type != null);

			throw new NoSuchMethodException();
		}
	}

	private Method similarMethod(String name, Class<?>[] types)
			throws NoSuchMethodException {
		Class<?> type = type();
		for (Method method : type.getMethods()) {
			if (isSimilarSignature(method, name, types)) {
				return method;
			}
		}
		do {
			for (Method method : type.getDeclaredMethods()) {
				if (isSimilarSignature(method, name, types)) {
					return method;
				}
			}

			type = type.getSuperclass();
		} while (type != null);

		throw new NoSuchMethodException("No similar method " + name
				+ " with params " + Arrays.toString(types)
				+ " could be found in type " + type() + ".");
	}

	private boolean isSimilarSignature(Method possiblyMatchingMethod,
			String desiredMethodName, Class<?>[] desiredParamTypes) {
		return possiblyMatchingMethod.getName().equals(desiredMethodName)
				&& match(possiblyMatchingMethod.getParameterTypes(),
						desiredParamTypes);
	}

	public APKReflect create() throws Exception {
		return create(new Object[0]);
	}

	public APKReflect create(Object... args) throws Exception {
		Class<?>[] types = types(args);

		try {
			Constructor<?> constructor = type().getDeclaredConstructor(types);
			return in(constructor, args);
		} catch (NoSuchMethodException e) {
			for (Constructor<?> constructor : type().getDeclaredConstructors()) {
				if (match(constructor.getParameterTypes(), types)) {
					return in(constructor, args);
				}
			}

			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <P> P as(Class<P> proxyType) {
		final boolean isMap = (object instanceof Map);
		final InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				String name = method.getName();

				try {
					return in(object).call(name, args).get();
				} catch (Exception e) {
					if (isMap) {
						Map<String, Object> map = (Map<String, Object>) object;
						int length = (args == null ? 0 : args.length);

						if (length == 0 && name.startsWith("get")) {
							return map.get(property(name.substring(3)));
						} else if (length == 0 && name.startsWith("is")) {
							return map.get(property(name.substring(2)));
						} else if (length == 1 && name.startsWith("set")) {
							map.put(property(name.substring(3)), args[0]);
							return null;
						}
					}

					throw e;
				}
			}
		};

		return (P) Proxy.newProxyInstance(proxyType.getClassLoader(),
				new Class[] { proxyType }, handler);
	}

	private static String property(String string) {
		int length = string.length();

		if (length == 0) {
			return "";
		} else if (length == 1) {
			return string.toLowerCase();
		} else {
			return string.substring(0, 1).toLowerCase() + string.substring(1);
		}
	}

	private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
		if (declaredTypes.length == actualTypes.length) {
			for (int i = 0; i < actualTypes.length; i++) {
				if (actualTypes[i] == EMPTY.class){
					continue;
				}
				if (wrapper(declaredTypes[i]).isAssignableFrom(
						wrapper(actualTypes[i]))){
					continue;
				}
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof APKReflect) {
			return object.equals(((APKReflect) obj).get());
		}

		return false;
	}

	private static APKReflect in(Constructor<?> constructor, Object... args)
			throws Exception {
		try {
			return in(accessible(constructor).newInstance(args));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private static APKReflect in(Method method, Object object, Object... args)
			throws Exception {
		try {
			accessible(method);

			if (method.getReturnType() == void.class) {
				method.invoke(object, args);
				return in(object);
			} else {
				return in(method.invoke(object, args));
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private static Object unwrap(Object object) {
		if (object instanceof APKReflect) {
			return ((APKReflect) object).get();
		}

		return object;
	}

	private static class EMPTY {
	}

	private static Class<?>[] types(Object... values) {
		if (values == null) {
			return new Class[0];
		}
		Class<?>[] result = new Class[values.length];
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			result[i] = value == null ?EMPTY.class : value.getClass();
		}
		return result;
	}

	private static Class<?> forName(String name) throws Exception {
		return Class.forName(name);
	}

	public Class<?> type() {
		if (isClass) {
			return (Class<?>) object;
		} else {
			return object.getClass();
		}
	}

	public static Class<?> wrapper(Class<?> type) {
		if (type == null) {
			return null;
		} else if (type.isPrimitive()) {
			if (boolean.class == type) {
				return Boolean.class;
			} else if (int.class == type) {
				return Integer.class;
			} else if (long.class == type) {
				return Long.class;
			} else if (short.class == type) {
				return Short.class;
			} else if (byte.class == type) {
				return Byte.class;
			} else if (double.class == type) {
				return Double.class;
			} else if (float.class == type) {
				return Float.class;
			} else if (char.class == type) {
				return Character.class;
			} else if (void.class == type) {
				return Void.class;
			}
		}

		return type;
	}

	@Override
	public String toString() {
		return object.toString();
	}
}
