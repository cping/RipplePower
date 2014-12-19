package org.ripple.power.ui.projector.core.graphics.component;

import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.projector.core.graphics.LComponent;
import org.ripple.power.ui.projector.core.graphics.LContainer;

public class LPanel extends LContainer {

	public LPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.customRendering = true;
	}

	public String getUIName() {
		return "Panel";
	}

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {

	}
}
