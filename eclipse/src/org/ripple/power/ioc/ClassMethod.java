
/**
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
 * @version 0.2
 */
package org.ripple.power.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.address.collection.MapArray;
import org.ripple.power.utils.CollectionUtils;
import org.ripple.power.utils.ReflectorUtils;


public class ClassMethod {

	private final MapArray fieldList = CollectionUtils.createArrayMap();

	private final MapArray methods = CollectionUtils.createArrayMap();

	private final MapArray fieldTypes = CollectionUtils.createArrayMap();

	private final MapArray getterMethods = CollectionUtils.createArrayMap();

	private final MapArray setterMethods = CollectionUtils.createArrayMap();

	private final Set nonGetters = CollectionUtils.createSet();

	/**
	 * 缓冲对应类数据，并设置是否保存Final方法
	 * 
	 * @param clazz
	 * @param includeFinalMethods
	 */
	public ClassMethod(final Class clazz, final boolean includeFinalMethods) {
		processClass(clazz, includeFinalMethods);
	}

	/**
	 * 缓冲对应类数据，并设置是否保存Final方法
	 * 
	 * @param clazz
	 * @param includeFinalMethods
	 */
	private void processClass(final Class clazz,
			final boolean includeFinalMethods) {
		final Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			final int mask = includeFinalMethods ? Modifier.PUBLIC
					: Modifier.PUBLIC | Modifier.FINAL;
			if (((methods[i].getModifiers() & mask) == Modifier.PUBLIC)
					&& (methods[i].getParameterTypes().length == 0)
					&& (!methods[i].getName().equals("<init>"))
					&& (!methods[i].getName().equals("<clinit>"))
					&& (methods[i].getReturnType() != void.class)) {
				final int fieldIndex = this.methods.size();
				addToMapping(methods[i], fieldIndex);
			} else if (((methods[i].getModifiers() & mask) == Modifier.PUBLIC)) {
				final int fieldIndex = this.methods.size();
				addToMapping(methods[i], fieldIndex);
			}
		}
		addFieldToMapping(clazz);
	}

	private void addToMapping(final Method method, final int index) {
		String methodName = ReflectorUtils.getMethodName(method);
		int offset = ReflectorUtils.getMethodOffsetAll(methodName);
		if (this.methods.containsKey(methodName)) {
			if (offset != 0 && this.nonGetters.contains(methodName)) {
				removeOldMethod(methodName);
				storeMethod(methodName, method);
				storeGetterSetter(method, methodName);
				this.nonGetters.remove(methodName);
			} else if (offset != 0) {
				storeGetterSetter(method, methodName);
			}
		} else {
			storeMethod(methodName, method);
			storeGetterSetter(method, methodName);
			if (offset == 0) {
				this.nonGetters.add(methodName);
			}
		}
	}

	/**
	 * 记录类中的Field
	 * 
	 * @param clazz
	 */
	private void addFieldToMapping(final Class clazz) {
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			addFieldToMapping(fields[i]);
		}
	}

	/**
	 * 记录Field
	 * 
	 * @param field
	 */
	private void addFieldToMapping(final Field field) {
		try {
			fieldList.put(field.getName(), field);
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public MapArray getFields() {
		return this.fieldList;
	}

	public MapArray getMethods() {
		return this.methods;
	}

	public MapArray getMethodFieldTypes() {
		return this.fieldTypes;
	}

	public MapArray getGetterMethods() {
		return this.getterMethods;
	}

	public MapArray getSetterMethods() {
		return this.setterMethods;
	}

	private void removeOldMethod(final String methodName) {
		this.methods.remove(methodName);
		this.fieldTypes.remove(methodName);
		this.getterMethods.remove(methodName);
	}

	private void storeMethod(final String methodName, final Method method) {
		this.methods.put(methodName, method);
	}

	private void storeGetterSetter(final Method method, final String methodName) {
		if (method.getName().startsWith("set")) {
			this.setterMethods.put(methodName, method);
			if (!fieldTypes.containsKey(methodName)) {
				if (method.getParameterTypes().length > 0) {
					this.fieldTypes.put(methodName,
							method.getParameterTypes()[0]);
				}
			}
		} else {
			this.getterMethods.put(methodName, method);
			this.fieldTypes.put(methodName, method.getReturnType());
		}
	}

}
