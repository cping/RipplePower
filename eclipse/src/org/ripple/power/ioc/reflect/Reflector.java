
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
package org.ripple.power.ioc.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.address.collection.ArrayIterator;
import org.address.collection.ConverterMap;
import org.address.collection.MapArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.ioc.ClassMethod;
import org.ripple.power.ioc.ClassUtils;
import org.ripple.power.utils.CollectionUtils;
import org.ripple.power.utils.ReflectorUtils;


final public class Reflector {

	private static final Map reflectorMap = Collections
			.synchronizedMap(new HashMap(1000));

	private Class clazz;

	private MapArray invokables;

	/**
	 * 从Object中获得一个Reflector对象
	 * 
	 * @param target
	 * @return
	 */
	public static Reflector getReflector(final Object target) {
		return getReflector(target.getClass());
	}

	/**
	 * 从Class中获得一个Reflector对象
	 * 
	 * @param clazz
	 * @return
	 */
	public static Reflector getReflector(final Class clazz) {
		Reflector reflector = (Reflector) reflectorMap.get(clazz);
		if (reflector == null) {
			reflector = new Reflector(clazz);
			reflectorMap.put(clazz, reflector);
		}
		return reflector;
	}

	/**
	 * 通过指定的String获得一个Reflector对象
	 * 
	 * @param className
	 * @return
	 */
	public static Reflector getReflector(final String className) {
		return getReflector(className, LSystem.classLoader);
	}

	/**
	 * 通过指定的String获得一个Reflector对象，并设定加载用的ClassLoader
	 * 
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static Reflector getReflector(final String className,
			ClassLoader classLoader) {
		if (className == null) {
			throw new IllegalArgumentException("class name is null!");
		}
		Reflector reflector = (Reflector) reflectorMap.get(className);
		if (reflector == null) {
			reflector = new Reflector(className, classLoader);
			reflectorMap.put(className, reflector);
		}
		return reflector;
	}

	/**
	 * 通过Class实例化Reflector
	 * 
	 * @param clazz
	 */
	private Reflector(Class clazz) {
		super();
		this.clazz = clazz;
		reflect();
	}

	/**
	 * 通过Class字符串实例化Reflector
	 * 
	 * @param className
	 * @param classLoader
	 */
	private Reflector(String className, ClassLoader classLoader) {
		super();
		if (className == null || className.trim().length() == 0) {
			this.clazz = void.class;
		} else {
			try {
				this.clazz = Class.forName(className, true, classLoader);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		reflect();
	}

	/**
	 * 构建类的构成反射
	 * 
	 */
	private void reflect() {
		this.invokables = CollectionUtils.createArrayMap();
		reflectConstructors();
		reflectMethods();
	}

	private void reflectConstructors() {
		Constructor[] c = clazz.getConstructors();
		int size = c.length;
		for (int i = 0; i < size; i++) {
			invokables.put(TypeArrays.getNamedTypeArray(c[i]), c[i]);
		}
	}

	/**
	 * 构建类的方法反射
	 * 
	 */
	private void reflectMethods() {
		ClassMethod clsMethod = ClassUtils.getFieldInspector(clazz);
		MapArray methods = (MapArray) clsMethod.getMethods();
		int size = methods.size();
		for (int i = 0; i < size; i++) {
			Method method = (Method) methods.get(i);
			invokables.put(TypeArrays.getNamedTypeArray(method), method);
		}
	}

	/**
	 * 匹配方法
	 * 
	 * @param name
	 * @param parameterTypes
	 * @param converterMap
	 * @param throwException
	 * @return
	 */
	public Method lookupMethod(String name, Class[] parameterTypes,
			ConverterMap converterMap, boolean throwException) {
		TypeArrays desired = new TypeArrays(name, parameterTypes);
		Object value = invokables.get(desired);
		if (value != null) {
			return (Method) value;
		}
		return (Method) lookupInvokable(desired, converterMap, throwException);
	}

	/**
	 * 匹配方法
	 * 
	 * @param name
	 * @param parameters
	 * @return
	 */
	public Method lookupMethod(String name, Class[] parameters) {
		Method method = lookupMethod(name, ReflectorUtils
				.parameterToTypeArray(parameters), ReflectorUtils.converterMap,
				true);
		return method;
	}

	/**
	 * 匹配构造
	 * 
	 * @param parameterTypes
	 * @param converterMap
	 * @param throwException
	 * @return
	 */
	public Constructor lookupConstructor(Class[] parameterTypes,
			ConverterMap converterMap, boolean throwException) {
		TypeArrays desired = new TypeArrays(TypeArrays.CONSTRUCTOR_METHOD_NAME,
				parameterTypes);
		Object object = invokables.get(desired);
		if (object != null) {
			return (Constructor) object;
		}
		return (Constructor) lookupInvokable(desired, converterMap,
				throwException);

	}

	/**
	 * 返回所有字段
	 * 
	 * @return
	 */
	public Set getFields() {
		return ReflectorUtils.getFields(clazz);
	}

	/**
	 * 比较两个Class数组，返回是非匹配
	 * 
	 * @param appleParams
	 * @param orangeParams
	 * @return
	 * @throws Exception
	 */
	public int compareTypes(Class[] appleParams, Class[] orangeParams)
			throws Exception {
		Boolean chose = null;
		for (int i = 0; i < appleParams.length; i++) {
			if (!appleParams[i].equals(orangeParams[i])) {
				if (orangeParams[i].isAssignableFrom(appleParams[i])) {
					if (chose != null) {
						throw new RuntimeException("chose != null!");
					}
					chose = Boolean.TRUE;
				} else if (appleParams[i].isAssignableFrom(orangeParams[i])) {
					if (chose != null) {
						throw new RuntimeException("chose != null!");
					}
					chose = Boolean.FALSE;
				}
			}
		}
		return (chose == null ? 0 : (chose.booleanValue() ? -1 : +1));
	}

	/**
	 * 返回一个可供Reflector调用的Object
	 * 
	 * @param desired
	 * @param converterMap
	 * @param throwException
	 * @return
	 */
	private AccessibleObject lookupInvokable(TypeArrays desired,
			ConverterMap converterMap, boolean throwException) {
		Invokable candidate = null;
		Invokable current = null;
		TypeArrays currentDescriptor = null;
		TypeArrays[] ntarrays = (TypeArrays[]) invokables.keySet().toArray(
				new TypeArrays[invokables.size()]);
		for (int i = 0; i < ntarrays.length; i++) {
			currentDescriptor = ntarrays[i];
			if (!currentDescriptor.isAliased()
					&& currentDescriptor.getName().equals(desired.getName())) {
				current = new Invokable((AccessibleObject) invokables
						.get(currentDescriptor));
				if (converterMap.typesAssignable(current.getParameterTypes(),
						desired.getParameterTypes())) {
					try {
						if (candidate == null
								|| compareTypes(currentDescriptor
										.getParameterTypes(), candidate
										.getParameterTypes()) < 0) {
							candidate = current;
						}
					} catch (Exception e) {
						if (candidate.getDeclaringClass().isAssignableFrom(
								current.getDeclaringClass())
								&& !candidate.getDeclaringClass().equals(
										current.getDeclaringClass())) {
							candidate = current;
						}
					}
				}
			}
		}
		if (candidate == null) {
			return null;
		}
		desired.setAliased(true);
		invokables.put(desired, candidate.unwrap());
		return candidate.unwrap();
	}

	/**
	 * 查询指定Class是否为目标反射类的实现
	 * 
	 * @param className
	 * @return
	 */
	public boolean isImplInterface(String className) {
		boolean result;
		try {
			result = isImplInterface(Class.forName(className));
		} catch (ClassNotFoundException e) {
			result = false;
		}
		return result;
	}

	/**
	 * 查询指定Class是否为目标反射类的实现
	 * 
	 * @param objClass
	 * @return
	 */
	public boolean isImplInterface(Class objClass) {
		Object[] names = ReflectorUtils.getInterfaceToObjects(clazz);
		if (names != null && names.length > 0) {
			for (Iterator it = new ArrayIterator(names); it.hasNext();) {
				String name = it.next().toString();
				if (name.equalsIgnoreCase(objClass.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查询指定方法是否存在
	 * 
	 * @param name
	 * @param types
	 * @return
	 */
	public boolean methodExists(String name, Class[] types) {
		return (lookupMethod(name, types, ReflectorUtils.converterMap, false) != null);
	}

	/**
	 * 查询指定构造是否存在
	 * 
	 * @param types
	 * @return
	 */
	public boolean constructorExists(Class[] types) {
		return (lookupConstructor(types, ReflectorUtils.converterMap, false) != null);
	}

	/**
	 * 传递数值到指定对象的方法中去
	 * 
	 * @param object
	 * @param methodName
	 * @param parameters
	 */
	public void setInvoke(final Object object, String methodName,
			Object[] parameters) {
		if (methodName.lastIndexOf(")") == -1) {
			methodName += ReflectorUtils.getConstruct(parameters);
		}
		String keyName = ReflectorUtils.getMatchSetMethod(clazz, methodName);
		Object beanObject = ReflectorUtils.doSetMethod(clazz, keyName);
		try {
			Method method = (Method) beanObject;
			method.invoke(object, parameters);
		} catch (Exception e) {
			throw new RuntimeException("setInvoke : ", e);
		}
	}

	/**
	 * 传递数值到Reflector的匹配方法中去
	 * 
	 * @param name
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public Object doInvoke(String name, Object[] parameters) throws Exception {
		return doInvoke(newInstance(), name, parameters);
	}

	/**
	 * 静态传递数值到指定对象的匹配方法中去（自动匹配类型）
	 * 
	 * @param object
	 * @param beanProperty
	 * @param value
	 */
	public static void doStaticInvokeRegister(final Object object,
			final String beanProperty, final String value) {
		Object[] beanObject = doStaticInvokeMatch(object.getClass(),
				beanProperty);
		Object[] cache = new Object[1];
		Method getter = (Method) beanObject[0];
		Method setter = (Method) beanObject[1];
		try {
			String methodType = getter.getReturnType().getName();
			if (methodType.equalsIgnoreCase("long")) {
				cache[0] = new Long(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("int")
					|| methodType.equalsIgnoreCase("integer")) {
				cache[0] = new Integer(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("short")) {
				cache[0] = new Short(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("float")) {
				cache[0] = new Float(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("double")) {
				cache[0] = new Double(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("boolean")) {
				cache[0] = new Boolean(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("java.lang.String")) {
				cache[0] = value;
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("java.io.InputStream")) {
			} else if (methodType.equalsIgnoreCase("char")) {
				cache[0] = new Character(value.charAt(0));
				setter.invoke(object, cache);
			}
		} catch (Exception ex) {
			throw new RuntimeException(beanProperty + " is " + ex.getMessage());
		}
	}

	/**
	 * 匹配静态注入的数据类型
	 * 
	 * @param clazz
	 * @param beanProperty
	 * @return
	 */
	final static public Object[] doStaticInvokeMatch(final Class clazz,
			final String beanProperty) {
		Object[] result = new Object[2];
		String nowPropertyName = ReflectorUtils.initialUppercase(beanProperty);
		String names[] = { ("set" + nowPropertyName).intern(),
				("get" + nowPropertyName).intern(),
				("is" + nowPropertyName).intern() };
		Method getter = null;
		Method setter = null;
		Method methods[] = clazz.getMethods();
		int length = methods.length;
		for (int i = 0; i < length; i++) {
			Method method = methods[i];
			if (!Modifier.isPublic(method.getModifiers()))
				continue;
			String methodName = method.getName().intern();
			for (int j = 0; j < names.length; j++) {
				String name = names[j];
				if (!name.equals(methodName))
					continue;
				if (methodName.startsWith("set")) {
					setter = method;
				} else {
					getter = method;
				}
			}
		}
		result[0] = getter;
		result[1] = setter;
		return result;
	}

	/**
	 * 静态传递数值到指定对象的匹配方法中去
	 * 
	 * @param object
	 * @param beanProperty
	 * @param parameters
	 */
	public static void doStaticInvoke(final Object object,
			final String beanProperty, final Object parameters) {
		Reflector reflector = Reflector.getReflector(object);
		Class clazz = reflector.getReflectedClass();
		String keyName = ReflectorUtils.getMatchSetMethod(clazz, beanProperty);
		Object beanObject = ReflectorUtils.doSetMethod(clazz, keyName);
		Object[] nowParameters = new Object[1];
		boolean isArgs = parameters instanceof Object[];
		if (isArgs) {
			nowParameters = (Object[]) parameters;
		}
		String type = ReflectorUtils.getSetMethodType(beanObject);
		try {
			if (!isArgs) {
				nowParameters[0] = ReflectorUtils.getReturnObject(type,
						parameters);
			}
			Method method = (Method) beanObject;
			method.invoke(object, nowParameters);
		} catch (Exception e) {
		}
	}

	/**
	 * 传递数值到指定对象的匹配方法中去
	 * 
	 * @param object
	 * @param methodName
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public Object doInvoke(Object object, String methodName, Object[] parameters)
			throws Exception {
		if (methodName.lastIndexOf(")") == -1) {
			methodName += ReflectorUtils.getConstruct(parameters);
		}
		methodName = ReflectorUtils.getMatchGetMethod(clazz, methodName);
		try {
			Method method = lookupMethod(methodName, ReflectorUtils
					.parameterToTypeArray(parameters),
					ReflectorUtils.converterMap, true);
			Object[] parametersToUse = ReflectorUtils.converterMap
					.convertParameters(method.getParameterTypes(), parameters);
			return method.invoke(object, parametersToUse);
		} catch (Exception ex) {
			throw new Exception((methodName
					+ " method can not be invoke ! exception : " + ex
					.getMessage()).intern());
		}
	}

	/**
	 * 实例化当前Reflector的目标对象
	 * 
	 * @param args
	 * @return
	 */
	public Object newInstance(Object[] args) {
		Constructor constructor = lookupConstructor(ReflectorUtils
				.parameterToTypeArray(args), ReflectorUtils.converterMap, true);
		Object[] parametersToUse = ReflectorUtils.converterMap
				.convertParameters(constructor.getParameterTypes(), args);
		try {
			return constructor.newInstance(parametersToUse);
		} catch (Exception e) {
			throw new RuntimeException("Reflector newInstance : ", e);
		}
	}

	/**
	 * 实例化当前Reflector的目标对象
	 * 
	 * @return
	 */
	public Object newInstance() {
		return newInstance(null);
	}

	/**
	 * 返回当前当前Reflector的作用类
	 * 
	 * @return
	 */
	public Class getReflectedClass() {
		return clazz;
	}
}
