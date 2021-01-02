/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package org.ripple.power.ui;

import java.awt.Dimension;
import java.awt.Insets;

public class RPUtils {

	private static Insets insets;

	public static Dimension newDim(int w, int h) {
		int titleWidth = 15;
		int titleHeight = 20;
		if (insets != null) {
			titleWidth = Math.min(titleWidth, insets.right + insets.left);
			titleHeight = Math.min(titleHeight, insets.bottom + insets.top);
		}
		return new Dimension(w + titleWidth, h + titleHeight);
	}

	public static void setInsets(Insets insets) {
		RPUtils.insets = insets;
	}

}
