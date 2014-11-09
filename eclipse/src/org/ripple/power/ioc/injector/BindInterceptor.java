package org.ripple.power.ioc.injector;

import java.util.Stack;
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

public class BindInterceptor implements Interceptor {

	private static ThreadLocal threadLocal = new ThreadLocal();

	public void before(Object key) {
		Stack stack = getStack();
		if (stack == null) {
			stack = new Stack();
			stack.push(key);
			threadLocal.set(stack);
		}
		else {
			stack.push(key);
		}
	}


	public void after(Object key) {
		Stack stack = getStack();
		stack.pop();
	}

	private Stack getStack() {
		return (Stack) threadLocal.get();
	}
	
	
	public void clear() {
		Stack stack = getStack();
		if (stack != null) {			
			stack.clear();
		}
	}

}
