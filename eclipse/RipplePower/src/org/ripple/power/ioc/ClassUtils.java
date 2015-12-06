package org.ripple.power.ioc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ripple.power.utils.CollectionUtils;

public class ClassUtils {

	final static Map<Object, Object> lazyMap = Collections
			.synchronizedMap(new HashMap<Object, Object>(1000));

	final static private String CLASS_FILE_SUFFIX = ".class";

	final static private char PACKAGE_SEPARATOR = '.';

	final static private Map<Object, Object> baseTypeMap = CollectionUtils
			.createMap(9);

	final static private Map<Object, Object> baseClassMap = CollectionUtils
			.createMap(9);

	// 类与对象的对应关系
	static {
		baseClassMap.put(Character.class, Character.TYPE);
		baseClassMap.put(Integer.class, Integer.TYPE);
		baseClassMap.put(Long.class, Long.TYPE);
		baseClassMap.put(Short.class, Short.TYPE);
		baseClassMap.put(Float.class, Float.TYPE);
		baseClassMap.put(Boolean.class, Boolean.TYPE);
		baseClassMap.put(Double.class, Double.TYPE);
		baseClassMap.put(Byte.class, Byte.TYPE);
		baseClassMap.put(Void.class, Void.TYPE);
	}

	// 对象与类的对应关系
	static {
		baseTypeMap.put(Boolean.TYPE, Boolean.class);
		baseTypeMap.put(Byte.TYPE, Byte.class);
		baseTypeMap.put(Character.TYPE, Character.class);
		baseTypeMap.put(Double.TYPE, Double.class);
		baseTypeMap.put(Float.TYPE, Float.class);
		baseTypeMap.put(Integer.TYPE, Integer.class);
		baseTypeMap.put(Long.TYPE, Long.class);
		baseTypeMap.put(Short.TYPE, Short.class);
		baseTypeMap.put(Void.TYPE, Void.class);
	}

	final static public String getClassToType(final Object object) {
		Class<? extends Object> clazz = object.getClass();
		Class<?> type = (Class<?>) baseClassMap.get(clazz);
		return type == null ? clazz.getName() : type.toString();
	}

	final static public Class<?> getTypeToClass(final Class<?> clazz) {
		return (Class<?>) baseClassMap.get(clazz);
	}

	final static public Map<Object, Object> getBaseTypes() {
		return baseTypeMap;
	}

	final public static boolean equals(ClassLoader cl, final ClassLoader other) {
		while (cl != null) {
			if (cl == other) {
				return true;
			}
			cl = cl.getParent();
		}
		return false;
	}

	public static String getResourcePath(String path, String extension) {
		if (extension == null) {
			return path;
		}
		extension = "." + extension;
		if (path.endsWith(extension)) {
			return path;
		}
		return path.replace('.', '/') + extension;
	}

	public static String getClassFileName(Class<?> clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	final static public ClassMethod getFieldInspector(final Class<?> clazz) {
		Object object = lazyMap.get(clazz);
		if (object == null) {
			object = new ClassMethod(clazz, false);
			lazyMap.put(clazz, object);
		}
		return (ClassMethod) object;
	}
}
