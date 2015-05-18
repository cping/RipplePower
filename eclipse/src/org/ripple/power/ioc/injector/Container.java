/**
 * 
 * Copyright 2008 
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

package org.ripple.power.ioc.injector;

import org.ripple.power.ioc.injector.attribute.AttributeInjectorBuilder;

public interface Container extends Start {

	public void inject(Object key, Object target);

	public void inject(Object target);

	public Object getAttributeValue(Object key);

	public Container addInjector(Object key, Injector injector);

	public Container addBind(Object key, Bind dependency);

	public Object getInstance(Object key);

	public Container addInstanceBind(Object key, Object instance);

	public AttributeInjectorBuilder addAttributeInjector(Object key);

	public ClassBind addClassBind(Object key, Class<?> classDependecy);

	public Bind getBind(Object key);
}
