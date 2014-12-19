package org.ripple.power.ui.projector.action.sprite.effect;

import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimer;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.graphics.geom.Vector2D;
import org.ripple.power.ui.projector.action.map.Config;
import org.ripple.power.ui.projector.action.map.Field2D;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class SplitEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2D v1, v2;

	private float alpha;

	private int width, height, halfWidth, halfHeight, multiples, direction;

	private boolean visible, complete, special;

	private RectBox limit;

	private LImage image;

	private LTimer timer;

	public SplitEffect(String fileName, int d) {
		this(new LImage(fileName), d);
	}

	public SplitEffect(LImage t, int d) {
		this(t, LSystem.screenRect, d);
	}

	public SplitEffect(LImage t, RectBox limit, int d) {
		this.image = t;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.multiples = 2;
		this.direction = d;
		this.limit = limit;
		this.timer = new LTimer(10);
		this.visible = true;
		this.v1 = new Vector2D();
		this.v2 = new Vector2D();
		switch (direction) {
		case Config.UP:
		case Config.DOWN:
			special = true;
		case Config.TLEFT:
		case Config.TRIGHT:
			v1.set(0, 0);
			v2.set(halfWidth, 0);
			break;
		case Config.LEFT:
		case Config.RIGHT:
			special = true;
		case Config.TUP:
		case Config.TDOWN:
			v1.set(0, 0);
			v2.set(0, halfHeight);
			break;
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (!complete) {
			if (timer.action(elapsedTime)) {
				switch (direction) {
				case Config.LEFT:
				case Config.RIGHT:
				case Config.TLEFT:
				case Config.TRIGHT:
					v1.move_multiples(Field2D.TLEFT, multiples);
					v2.move_multiples(Field2D.TRIGHT, multiples);
					break;
				case Config.UP:
				case Config.DOWN:
				case Config.TUP:
				case Config.TDOWN:
					v1.move_multiples(Field2D.TUP, multiples);
					v2.move_multiples(Field2D.TDOWN, multiples);
					break;
				}

				if (special) {
					if (!limit.intersects((int) v1.x, (int) v1.y, halfHeight,
							halfWidth)
							&& !limit.intersects((int) v2.x, (int) v2.y,
									halfHeight, halfWidth)) {
						this.complete = true;
					}
				} else if (!limit.intersects((int) v1.x, (int) v1.y, halfWidth,
						halfHeight)
						&& !limit.intersects((int) v2.x, (int) v2.y, halfWidth,
								halfHeight)) {
					this.complete = true;
				}
			}
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (!complete) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			final int x1 = (int) (v1.x + getX());
			final int y1 = (int) (v1.y + getY());

			final int x2 = (int) (v2.x + getX());
			final int y2 = (int) (v2.y + getY());

			switch (direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				g.drawImage(image, x1, y1, width, halfHeight, 0, 0, width,
						halfHeight);
				g.drawImage(image, x2, y2, width, halfHeight, 0, halfHeight,
						width, height);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				g.drawImage(image, x1, y1, halfWidth, height, 0, 0,
						halfWidth, height);
				g.drawImage(image, x2, y2, halfWidth, height, halfWidth, 0,
						width, height);
				break;

			}

			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1f);
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float a) {
		this.alpha = a;
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
