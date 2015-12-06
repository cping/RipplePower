package org.ripple.power.ui.projector.action.sprite.effect;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.Config;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class OutEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LImage image;

	private boolean visible, complete;

	private float alpha;

	private int width, height;

	private int type, multiples;

	private RectBox limit;

	public OutEffect(String fileName, int code) {
		this(new LImage(fileName), code);
	}

	public OutEffect(LImage t, int code) {
		this(t, LSystem.screenRect, code);
	}

	public OutEffect(LImage t, RectBox limit, int code) {
		this.image = t;
		this.type = code;
		this.width = t.getWidth();
		this.height = t.getHeight();
		this.multiples = 1;
		this.limit = limit;
		this.visible = true;
	}

	public void update(long elapsedTime) {
		if (!complete) {
			switch (type) {
			case Config.DOWN:
				move_45D_down(multiples);
				break;
			case Config.UP:
				move_45D_up(multiples);
				break;
			case Config.LEFT:
				move_45D_left(multiples);
				break;
			case Config.RIGHT:
				move_45D_right(multiples);
				break;
			case Config.TDOWN:
				move_down(multiples);
				break;
			case Config.TUP:
				move_up(multiples);
				break;
			case Config.TLEFT:
				move_left(multiples);
				break;
			case Config.TRIGHT:
				move_right(multiples);
				break;
			}
			if (!limit.intersects(x(), y(), width, height)) {
				complete = true;
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public int getHeight() {
		return width;
	}

	public int getWidth() {
		return height;
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (!complete) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			g.drawImage(image, x(), y());
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1);
			}
		}
	}

	public void setAlpha(float a) {
		this.alpha = a;
	}

	public float getAlpha() {
		return alpha;
	}

	public LImage getBitmap() {
		return image;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

}
