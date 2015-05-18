package org.ripple.power.ui.projector.action.sprite.effect;

import java.awt.Color;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class FadeEffect extends LObject implements ISprite {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LColor color;

	public int time;

	public int currentFrame;

	public int type;

	public boolean stop;

	private int opacity, offsetX, offsetY;

	private int width;

	private int height;

	private boolean visible;

	public static FadeEffect getInstance(int type, LColor c) {
		return new FadeEffect(c, 60, type, LSystem.screenRect.getWidth(),
				LSystem.screenRect.getHeight());

	}

	public FadeEffect(LColor c, int delay, int type, int w, int h) {
		this.visible = true;
		this.type = type;
		this.setDelay(delay);
		this.setColor(c);
		this.width = w;
		this.height = h;
	}

	public float getDelay() {
		return time;
	}

	public void setDelay(int delay) {
		this.time = delay;
		if (type == TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setVisible(boolean visible) {
		this.opacity = visible ? 255 : 0;
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getOpacity() {
		return opacity;
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (stop) {
			return;
		}
		double op = ((double) currentFrame / (double) time) * 255;
		setOpacity((int) op);
		if (opacity > 0) {
			Color tempColor = g.getColor();
			g.setColor(new Color(color.getRed(), color.getGreen(), color
					.getBlue(), opacity));
			g.fillRect((int) (offsetX + this.x()), (int) (offsetY + this.y()),
					width, height);
			g.setColor(tempColor);
			return;
		}
	}

	public void update(long timer) {
		if (type == TYPE_FADE_IN) {
			currentFrame--;
			if (currentFrame == 0) {
				setOpacity(0);
				stop = true;
			}
		} else {
			currentFrame++;
			if (currentFrame == time) {
				setOpacity(0);
				stop = true;
			}
		}
	}

	public float getAlpha() {
		return 0;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public LImage getBitmap() {
		return null;
	}

	public void dispose() {

	}
}
