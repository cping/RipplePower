/**
 * 
 * Copyright 2008 - 2009
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
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

class Invokable extends Object {

	private final Method _method;
	private final Constructor<?> _constructor;

	public Invokable(AccessibleObject accessibleObject) {
		if (accessibleObject instanceof Method) {
			this._method = (Method) accessibleObject;
			this._constructor = null;
		} else if (accessibleObject instanceof Constructor) {
			this._constructor = (Constructor<?>) accessibleObject;
			this._method = null;
		} else {
			throw new IllegalArgumentException(accessibleObject
					+ " is not a Constructor or Method");
		}
	}

	public Class<?>[] getParameterTypes() {
		if (_method != null) {
			return _method.getParameterTypes();
		} else {
			return _constructor.getParameterTypes();
		}
	}

	public Class<?> getDeclaringClass() {
		if (_method != null) {
			return _method.getDeclaringClass();
		} else {
			return _constructor.getDeclaringClass();
		}
	}

	public AccessibleObject unwrap() {
		if (_method != null) {
			return _method;
		} else {
			return _constructor;
		}
	}
}
