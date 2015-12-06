package org.ripple.power.ui.projector.action.sprite;

import java.io.Serializable;

import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.core.LRelease;

public interface ISprite extends Serializable, LRelease {

	public static final int TYPE_FADE_IN = 0;

	public static final int TYPE_FADE_OUT = 1;

	public abstract LImage getBitmap();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract float getAlpha();

	public abstract int x();

	public abstract int y();

	public abstract double getX();

	public abstract double getY();

	public abstract void setVisible(boolean visible);

	public abstract boolean isVisible();

	public abstract void createUI(LGraphics g);

	public abstract void update(long elapsedTime);

	public abstract int getLayer();

	public abstract void setLayer(int layer);

	public abstract RectBox getCollisionBox();
}
