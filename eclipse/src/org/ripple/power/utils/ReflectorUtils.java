/**
 * 
 * Copyright 2008
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package org.ripple.power.utils;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ripple.power.collection.ConverterMap;
import org.ripple.power.collection.MapArray;
import org.ripple.power.ioc.ClassUtils;
import org.ripple.power.ioc.reflect.ClassConverter;
import org.ripple.power.ioc.reflect.Reflector;

public class ReflectorUtils {

	final static public String FAIL_TAG = "FAIL";

	final static public ConverterMap converterMap = CollectionUtils
			.createConverterMap();

	final static private Map<Object, Object> interfaceMap = CollectionUtils
			.createMap();

	final static private Class<?>[] EMPTY_CLASS = new Class[0];

	final static private Object[] EMPTY_OBJECT = new Object[0];

	static {
		converterMap.store(Class.class, new ClassConverter());
	}

	final static public Object newInstance(final Class<?> targetClass) {
		return Reflector.getReflector(targetClass).newInstance();
	}

	final static public Object getInvoke(Class<?> clazz, String name) {
		String nfieldName = name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return getNotPrefixInvoke(clazz, ("get" + nfieldName).intern());

	}

	final static public Object getInvoke(Object object, String name) {
		String nfieldName = name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return getNotPrefixInvoke(object, ("get" + nfieldName).intern());

	}

	final static public Object getNotPrefixInvoke(Class<?> clazz, String name) {
		Reflector reflector = Reflector.getReflector(clazz);
		try {
			return reflector.doInvoke(name, null);
		} catch (Exception e) {
			return null;
		}
	}

	final static public Object getNotPrefixInvoke(Object object, String name) {
		Reflector reflector = Reflector.getReflector(object.getClass());
		try {
			return reflector.doInvoke(object, name, null);
		} catch (Exception e) {
			return null;
		}
	}

	final static public Object getNotPrefixInvoke(Object object, String name,
			Object[] args) {
		Reflector reflector = Reflector.getReflector(object.getClass());
		try {
			return reflector.doInvoke(object, name, args);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?> getParameterType(Class<?> targetClass,
			String attributeName, String preffix) {
		String setName = preffix + initialUppercase(attributeName);
		Collection<Object> methods = CollectionUtils
				.createCollection(targetClass.getMethods());
		for (Iterator<Object> it = methods.iterator(); it.hasNext();) {
			Method method = (Method) it.next();
			if (setName.equals(method.getName())
					&& method.getParameterTypes().length == 1) {
				Collection<Object> collection = CollectionUtils
						.createCollection(method.getParameterTypes());
				return (Class<?>) CollectionUtils.first(collection);
			}
		}
		return null;

	}

	public static String getMethodName(Method method) {
		boolean flag = false;
		char temp;
		char[] methods = method.toString().toCharArray();
		int size = methods.length;
		StringBuffer sbr = new StringBuffer(size);
		for (int i = 0; i < size; i++) {
			temp = methods[i];
			if (temp == '(') {
				flag = true;
			}
			if (flag) {
				sbr.append(temp);
			}
			if (temp == ')') {
				break;
			}
		}
		return (method.getName() + sbr.toString()).intern();
	}

	public static String initialUppercase(final String keyName) {
		byte[] array = keyName.getBytes();
		array[0] = (byte) Character.toUpperCase((char) array[0]);
		return new String(array);
	}

	final public static String getSetMethodType(final Object property) {
		if (property == null)
			return null;
		String result = property.toString();
		result = result.substring(result.indexOf("(") + 1, result.length() - 1);
		return result;
	}

	public void setField(final Object object, final String name,
			final Object value) throws Exception {
		Class<?> clazz = object.getClass();
		MapArray fields = ClassUtils.getFieldInspector(clazz).getFields();
		Field field = (Field) fields.get(name);
		Object[] parameters = new Object[] { value };
		try {
			field = clazz.getField(name);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		Class<?>[] targetType = new Class[] { field.getType() };
		Object[] valueToUse = ReflectorUtils.converterMap.convertParameters(
				targetType, parameters);
		try {
			field.set(object, valueToUse[0]);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	final public static Object getField(final Object object, final String name)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		MapArray fields = ClassUtils.getFieldInspector(clazz).getFields();
		Field field = (Field) fields.get(name);
		return field.get(object);
	}

	public static boolean isImplInterface(Class<?> classSource, Class<?> target) {
		return Reflector.getReflector(classSource).isImplInterface(target);
	}

	public static Class<?>[] parameterToTypeArray(Object[] parameters) {
		if (parameters == null) {
			return null;
		}
		Class<?>[] types = new Class[parameters.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = (parameters[i] != null ? parameters[i].getClass() : null);
		}
		return types;
	}

	protected static boolean isFailMath(final String beanName) {
		return beanName.endsWith(FAIL_TAG);
	}

	public static Object invokeContructor(Class<?> clazz,
			Collection<Object> instances) {
		return getReturnObjects(clazz, instances != null ? instances.toArray()
				: null);
	}

	final public static Object getReturnObject(Class<?> methodType, Object value) {
		return getReturnObject(FileUtils.getExtension(methodType.getName()),
				value);
	}

	final public static Object getReturnObjects(Class<?> methodType,
			Object[] value) {
		return getReturnObjects(FileUtils.getExtension(methodType.getName()),
				value);
	}

	final public static Object getReturnObjects(String classType,
			Object[] values) {
		Object _result = null;
		if (classType.equals(String[].class))
			return values;
		if (classType.equals(int[].class)) {
			int[] array = new int[values.length];
			for (int i = 0; i < array.length; i++) {
				array[i] = Integer.parseInt(values[i].toString());
			}
			return array;
		}
		if (classType.equals(Integer[].class)) {
			Integer[] array = new Integer[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Integer.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(boolean[].class)) {
			boolean[] array = new boolean[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Boolean.getBoolean(values[i].toString());
			return array;
		}
		if (classType.equals(Boolean[].class)) {
			Boolean[] array = new Boolean[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Boolean.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(long[].class)) {
			long[] array = new long[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Long.parseLong(values[i].toString());
			return array;
		}
		if (classType.equals(Long[].class)) {
			Long[] array = new Long[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Long.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(float[].class)) {
			float[] array = new float[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Float.parseFloat(values[i].toString());
			return array;
		}
		if (classType.equals(Float[].class)) {
			Float[] array = new Float[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Float.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(double[].class)) {
			double[] array = new double[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Double.parseDouble(values[i].toString());
			return array;
		}
		if (classType.equals(Double[].class)) {
			Double[] array = new Double[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Double.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(short[].class)) {
			short[] array = new short[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Short.parseShort(values[i].toString());
			return array;
		}
		if (classType.equals(Short[].class)) {
			Short[] array = new Short[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Short.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(byte[].class)) {
			byte[] array = new byte[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Byte.parseByte(values[i].toString());
			return array;
		}
		if (classType.equals(Byte[].class)) {
			Byte[] array = new Byte[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = Byte.valueOf(values[i].toString());
			return array;
		}
		if (classType.equals(char[].class)) {
			char[] array = new char[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = values[i].toString().charAt(0);
			return array;
		}
		if (classType.equals(Character[].class)) {
			Character[] array = new Character[values.length];
			for (int i = 0; i < array.length; i++)
				array[i] = new Character(values[i].toString().charAt(0));
			return array;
		}
		return _result;
	}

	final public static String getConstruct(final Object[] parameters) {
		int size;
		if (parameters == null || (size = parameters.length) == 0) {
			return "()";
		}
		StringBuffer sbr = new StringBuffer(20);
		int index = 0;
		do {
			Object parameter = parameters[index];
			sbr.append(ClassUtils.getClassToType(parameter));
			sbr.append(",");
			index++;
		} while (index < size);
		return ("(" + sbr.delete(sbr.length() - 1, sbr.length()).toString() + ")")
				.intern();
	}

	final public static Object getReturnObject(final String classType,
			final Object value) {
		if (value == null)
			return null;
		Object result = null;
		String returnType = classType.trim().toLowerCase();
		if (returnType.indexOf("long") != -1) {
			result = new Long(value.toString());
		} else if (returnType.indexOf("int") != -1) {
			if (value instanceof Long) {
				result = new Integer(((Long) value).intValue());
			} else {
				result = new Integer(value.toString());
			}
		} else if (returnType.indexOf("integer") != -1) {
			result = new Integer(value.toString());
		} else if (returnType.indexOf("short") != -1) {
			result = new Short(value.toString());
		} else if (returnType.indexOf("float") != -1) {
			result = new Float(value.toString());
		} else if (returnType.indexOf("double") != -1) {
			result = new Double(value.toString());
		} else if (returnType.indexOf("boolean") != -1) {
			result = new Boolean(value.toString());
		} else if (returnType.indexOf("bigdecimal") != -1) {
			result = new BigDecimal(value.toString());
		} else if (returnType.indexOf("string") != -1) {
			result = value;
		} else if (returnType.indexOf("date") != -1) {
			if (value instanceof Date) {
				result = (Date) value;
			} else {
				result = stringToDate(value.toString());
			}
		} else if (returnType.indexOf("calendar") != -1) {
			result = DateUtils.toCalendar(value.toString());
		} else if (returnType.indexOf("inputstream") != -1) {
			result = (InputStream) value;
		} else if (returnType.indexOf("blob") != -1) {
			result = (Blob) value;
		} else if (returnType.indexOf("clob") != -1) {
			result = (Clob) value;
		} else if (returnType.indexOf("char") != -1) {
			result = (new Character(value.toString().charAt(0)));
		} else if (returnType.indexOf("byte") != -1) {
			result = (Byte) value;
		} else if (returnType.indexOf("object[]") != -1) {
			result = (Object[]) value;
		} else if (returnType.indexOf("array") != -1) {
			result = (Array) value;
		} else if (returnType.indexOf("date") != -1) {
			result = (Date) value;
		} else {
			result = value;
		}
		return result;
	}

	private static Date stringToDate(String str) {
		if (str == null) {
			return null;
		}
		DateFormat defaultDate = DateFormat.getDateInstance();
		Date date = null;
		try {
			date = defaultDate.parse(str);
		} catch (ParseException pe) {
			date = new Date();
		}
		return date;
	}

	final static public Class<?> getReturnClass(String typeName) {
		Class<?> result = null;
		if (typeName.equalsIgnoreCase("long")) {
			result = long.class;
		} else if (typeName.equalsIgnoreCase("int")) {
			result = int.class;
		} else if (typeName.equalsIgnoreCase("integer")) {
			result = Integer.class;
		} else if (typeName.equalsIgnoreCase("short")) {
			result = short.class;
		} else if (typeName.equalsIgnoreCase("float")) {
			result = float.class;
		} else if (typeName.equalsIgnoreCase("double")) {
			result = double.class;
		} else if (typeName.equalsIgnoreCase("boolean")) {
			result = boolean.class;
		} else if (typeName.equalsIgnoreCase("string")) {
			result = String.class;
		} else if (typeName.equalsIgnoreCase("calendar")) {
			result = Calendar.class;
		} else if (typeName.equalsIgnoreCase("inputstream")) {
			result = InputStream.class;
		} else if (typeName.equalsIgnoreCase("blob")) {
			result = Blob.class;
		} else if (typeName.equalsIgnoreCase("clob")) {
			result = Clob.class;
		} else if (typeName.equalsIgnoreCase("char")) {
			result = char.class;
		} else if (typeName.equalsIgnoreCase("character")) {
			result = Character.class;
		} else if (typeName.equalsIgnoreCase("byte")) {
			result = byte.class;
		} else if (typeName.equalsIgnoreCase("object[]")) {
			result = Object[].class;
		} else if (typeName.equalsIgnoreCase("byte[]")) {
			result = byte[].class;
		} else if (typeName.equalsIgnoreCase("array")) {
			result = Array.class;
		} else {
			result = Object.class;
		}
		return result;
	}

	public static boolean nullSafeEquals(Object apple, Object orange) {
		if (apple == null && orange == null) {
			return true;
		}
		if (apple == null || orange == null) {
			return false;
		}
		return (apple.equals(orange) && orange.equals(apple));
	}

	public static Object checkAssignment(Class<?> targetClass, Object rawObject) {
		if (rawObject == null) {
			return null;
		}
		checkAssignment(targetClass, rawObject.getClass());
		return rawObject;
	}

	public static void checkAssignment(Class<?> targetClass, Class<?> clazz) {
		if (!targetClass.isAssignableFrom(clazz)) {
			throwClassCastException(targetClass, clazz);
		}
	}

	private static void throwClassCastException(Class<?> targetClass,
			Class<?> clazz) {
		throw new ClassCastException("Cannot assign an object of type " + clazz
				+ " to an object of type " + targetClass);
	}

	public static Object invokeInit(Class<?> clazz, Object arg)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Object[] args = { arg };
		return invokeInit(clazz, args);
	}

	public static Object invokeInit(Class<?> clazz, Object[] args)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		if (null == args) {
			args = EMPTY_OBJECT;
		}
		int arguments = args.length;
		Class<?> parameterTypes[] = new Class[arguments];
		for (int i = 0; i < arguments; i++) {
			parameterTypes[i] = args[i].getClass();
		}
		return invokeInit(clazz, args, parameterTypes);

	}

	public static Object invokeInit(Class<?> clazz, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		if (parameterTypes == null) {
			parameterTypes = EMPTY_CLASS;
		}
		if (args == null) {
			args = EMPTY_OBJECT;
		}
		Constructor<?> ctor = getMatchingAccessibleConstructor(clazz,
				parameterTypes);
		if (null == ctor) {
			return null;
		}
		return ctor.newInstance(args);
	}

	public static Object invokeInitExact(Class<?> clazz, Object arg)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Object[] args = { arg };
		return invokeInitExact(clazz, args);
	}

	public static Object invokeInitExact(Class<?> clazz, Object[] args)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		if (null == args) {
			args = EMPTY_OBJECT;
		}
		int arguments = args.length;
		Class<?> parameterTypes[] = new Class<?>[arguments];
		for (int i = 0; i < arguments; i++) {
			parameterTypes[i] = args[i].getClass();
		}
		return invokeInitExact(clazz, args, parameterTypes);
	}

	public static Object invokeInitExact(Class<?> clazz, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {

		if (args == null) {
			args = EMPTY_OBJECT;
		}

		if (parameterTypes == null) {
			parameterTypes = EMPTY_CLASS;
		}

		Constructor<?> ctor = getAccessible(clazz, parameterTypes);
		if (null == ctor) {
			return null;
		}
		return ctor.newInstance(args);

	}

	public static Constructor<?> getAccessible(Class<?> clazz,
			Class<?> parameterType) {
		Class<?>[] parameterTypes = { parameterType };
		return getAccessible(clazz, parameterTypes);
	}

	public static Constructor<?> getAccessible(Class<?> clazz,
			Class<?>[] parameterTypes) {
		try {
			return getAccessible(clazz.getConstructor(parameterTypes));
		} catch (NoSuchMethodException e) {
			return (null);
		}
	}

	public static Constructor<?> getAccessible(Constructor<?> ctor) {
		if (ctor == null) {
			return (null);
		}
		if (!Modifier.isPublic(ctor.getModifiers())) {
			return (null);
		}
		Class<?> clazz = ctor.getDeclaringClass();
		if (Modifier.isPublic(clazz.getModifiers())) {
			return (ctor);
		}
		return null;
	}

	private static Constructor<?> getMatchingAccessibleConstructor(
			Class<?> clazz, Class<?>[] parameterTypes) {
		try {
			Constructor<?> ctor = clazz.getConstructor(parameterTypes);
			try {
				ctor.setAccessible(true);
			} catch (SecurityException se) {
			}
			return ctor;
		} catch (NoSuchMethodException e) {
		}
		int paramSize = parameterTypes.length;
		Constructor<?>[] ctors = clazz.getConstructors();
		for (int i = 0, size = ctors.length; i < size; i++) {
			Class<?>[] ctorParams = ctors[i].getParameterTypes();
			int ctorParamSize = ctorParams.length;
			if (ctorParamSize == paramSize) {
				boolean match = true;
				for (int n = 0; n < ctorParamSize; n++) {
					if (!isAssignmentCompatible(ctorParams[n],
							parameterTypes[n])) {
						match = false;
						break;
					}
				}
				if (match) {
					Constructor<?> ctor = getAccessible(ctors[i]);
					if (ctor != null) {
						try {
							ctor.setAccessible(true);
						} catch (SecurityException se) {
						}
						return ctor;
					}
				}
			}
		}
		return null;
	}

	public static final boolean isAssignmentCompatible(Class<?> parameterType,
			Class<?> parameterization) {
		if (parameterType.isAssignableFrom(parameterization)) {
			return true;
		}
		if (parameterType.isPrimitive()) {
			Class<?> parameterWrapperClazz = getWrapper(parameterType);
			if (parameterWrapperClazz != null) {
				return parameterWrapperClazz.equals(parameterization);
			}
		}
		return false;
	}

	public static Class<?> getWrapper(Class<?> classType) {
		if (boolean.class.equals(classType)) {
			return Boolean.class;
		} else if (float.class.equals(classType)) {
			return Float.class;
		} else if (long.class.equals(classType)) {
			return Long.class;
		} else if (int.class.equals(classType)) {
			return Integer.class;
		} else if (short.class.equals(classType)) {
			return Short.class;
		} else if (byte.class.equals(classType)) {
			return Byte.class;
		} else if (double.class.equals(classType)) {
			return Double.class;
		} else if (char.class.equals(classType)) {
			return Character.class;
		} else {
			return null;
		}
	}

	public static int arrayHashCode(Object[] objects) {
		int hc = 0;
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				hc += (objects[i] != null ? (objects[i].hashCode() * 31) : 0);
			}
		}
		return hc;
	}

	public static Object[] getInterfaceToObjects(Object object) {
		Set<?> set = getInterfaceToSet(object.getClass());
		return (Object[]) set.toArray(new Object[set.size()]);
	}

	public static Object[] getInterfaceToObjects(Class<?> clazz) {
		return getInterfaceToSet(clazz).toArray();
	}

	final static public Set<?> getInterfaceToSet(Class<?> clazz) {
		if (clazz.isInterface()) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Set<Object> interfaceSet = (Set<Object>) interfaceMap.get(clazz);
		if (interfaceSet == null) {
			interfaceSet = CollectionUtils.createSet();
			for (Class<?> target = clazz; target != Object.class; target = target
					.getSuperclass()) {
				Class<?>[] interfaces = target.getInterfaces();
				for (int i = 0; i < interfaces.length; ++i) {
					interfaceSet.add(interfaces[i].getName());
				}
			}
			interfaceMap.put(clazz, interfaceSet);
		}
		return interfaceSet;
	}

	final static public Method doMethod(Class<?> clazz, String name) {
		return doMethod(clazz, name, 0);
	}

	final static public Method doSetMethod(final Class<?> clazz,
			final String name) {
		return doMethod(clazz, name, 1);
	}

	final static public Method doGetMethod(Class<?> clazz, String name) {
		return doMethod(clazz, name, 2);
	}

	final static public String getParameter(String keyName) {
		String result = keyName;
		result = result.substring(result.indexOf("(") + 1, result.length() - 1);
		return result;
	}

	final static private Method doMethod(final Class<?> clazz,
			final String name, final int flag) {
		MapArray asmMethods = ClassUtils.getFieldInspector(clazz).getMethods();
		Method method = (Method) asmMethods.get(name);
		if (flag == 0 || method != null) {
			return method;
		} else {
			String keyName = ReflectorUtils.getParameter(name);
			Reflector reflector = Reflector.getReflector(keyName);
			keyName = StringUtils.replace(
					StringUtils.replace(name, keyName, ""), "()", "");
			Set<?> entrys = asmMethods.entrySet();
			for (Iterator<?> it = entrys.iterator(); it.hasNext();) {
				Entry<?, ?> entry = (Entry<?, ?>) it.next();
				String methodName = (String) entry.getKey();
				if (methodName.startsWith(keyName)) {
					String result = ReflectorUtils.getParameter(methodName);
					if (reflector.isImplInterface(result)) {
						return (Method) entry.getValue();
					}
				}
			}
		}
		int offset = -1;
		if (method == null) {
			Set<?> entrys = asmMethods.entrySet();
			for (Iterator<?> it = entrys.iterator(); it.hasNext();) {
				Entry<?, ?> entry = (Entry<?, ?>) it.next();
				String methodName = (String) entry.getKey();
				if (flag == 1) {
					offset = ReflectorUtils.getMethodOffset(methodName);
				} else if (flag == 2) {
					offset = ReflectorUtils.getMethodOffget(methodName);
				}
				if (offset > 0) {
					String nowName = methodName.substring(offset,
							methodName.length());
					if (nowName.equals(initialUppercase(name))) {
						return (Method) entry.getValue();
					}
				}
			}
		}
		return method;
	}

	final static public String getMatchSetMethod(final Class<?> clazz,
			final String name) {
		Set<?> set = ClassUtils.getFieldInspector(clazz).getSetterMethods()
				.keySet();
		if (name.startsWith("set")) {
			return name;
		}
		for (Iterator<?> it = set.iterator(); it.hasNext();) {
			String methodName = (String) it.next();
			int offset = ReflectorUtils.getMethodOffset(methodName);
			if (offset > 0) {
				String nowName = methodName.substring(offset,
						methodName.length());
				if (nowName.equals(initialUppercase(name))) {

					return methodName;
				}
			}
		}
		return name;
	}

	final static public String getMatchGetMethod(final Class<?> clazz,
			final String name) {
		Set<?> set = ClassUtils.getFieldInspector(clazz).getGetterMethods()
				.keySet();
		if (name.startsWith("get") || name.startsWith("is")) {
			return name;
		}
		for (Iterator<?> it = set.iterator(); it.hasNext();) {
			String methodName = (String) it.next();
			int offset = ReflectorUtils.getMethodOffget(methodName);
			if (offset > 0) {
				String nowName = methodName.substring(offset,
						methodName.length());
				if (nowName.equals(initialUppercase(name))) {
					return methodName;
				}
			}
		}
		return name;
	}

	final static public int getMethodOffget(final String methodName) {
		int offset;
		if (methodName.startsWith("is")) {
			offset = 2;
		} else if (methodName.startsWith("get")) {
			offset = 3;
		} else {
			offset = 0;
		}
		return offset;
	}

	final static public int getMethodOffset(final String methodName) {
		int offset;
		if (methodName.startsWith("set")) {
			offset = 3;
		} else {
			offset = 0;
		}
		return offset;
	}

	final static public int getMethodOffsetAll(final String methodName) {
		int offset;
		if (methodName.startsWith("is")) {
			offset = 2;
		} else if (methodName.startsWith("get") || methodName.startsWith("set")) {
			offset = 3;
		} else {
			offset = 0;
		}
		return offset;
	}

	@SuppressWarnings("unchecked")
	final static public Set<Object> getMethodNames(Class<?> clazz) {
		return ClassUtils.getFieldInspector(clazz).getMethods().keySet();
	}

	@SuppressWarnings("unchecked")
	final static public Set<Object> getFields(Class<?> clazz) {
		return ClassUtils.getFieldInspector(clazz).getFields().keySet();
	}

}
