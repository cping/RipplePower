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
package org.ripple.power.ioc.reflect;

import java.beans.PropertyEditor;

public class PropertyEditorConverter extends BaseConverter implements Converter {

	private PropertyEditor editor;

	public PropertyEditorConverter(PropertyEditor editor) {
		super(String.class);
		this.editor = editor;
	}

	protected Object doConversion(Object text) {
		editor.setAsText((String) text);
		return editor.getValue();
	}
}