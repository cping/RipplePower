package org.ripple.power.ui.projector.action.sprite.effect;

import java.awt.Color;

import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimer;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class ArcEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private int count;

	private int div = 10;

	private int turn = 1;

	private int[] sign = { 1, -1 };

	private int width, height;

	private LColor color;

	private boolean visible, complete;

	private LTimer timer;

	public ArcEffect(LColor c) {
		this(c, 0, 0, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public ArcEffect(LColor c, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.timer = new LTimer(200);
		this.color = c == null ? LColor.black : c;
		this.visible = true;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public boolean isComplete() {
		return complete;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (complete) {
			return;
		}
		if (this.count >= this.div) {
			this.complete = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (complete) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		if (count <= 1) {
			g.setColor(color);
			g.fillRect(x(), y(), width, height);
			g.resetColor();
		} else {
			g.setColor(color);
			int length = (int) Math.sqrt(Math.pow(width / 2, 2.0f) + Math.pow(height / 2, 2.0f));
			int x = x() + (width / 2 - length);
			int y = y() + (height / 2 - length);
			int w = width / 2 + length - x;
			int h = height / 2 + length - y;
			int deg = 360 / this.div * this.count;
			g.fillArc(x, y, w, h, 0, -360 - (this.sign[this.turn] * deg));
			g.resetColor();
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		this.complete = false;
		this.count = 0;
		this.turn = 1;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public LImage getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void dispose() {

	}

}
