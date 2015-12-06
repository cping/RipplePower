package org.ripple.power.ui.projector.action.sprite.effect;

import org.ripple.power.timer.LTimer;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class ScaleEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private int width, height;

	private boolean visible, complete;

	private LTimer timer;

	private LImage image;

	private int count;

	private int maxcount = 20;

	private int centerX, centerY;

	private boolean flag;

	public ScaleEffect(String fileName, boolean f) {
		this(new LImage(fileName), f);
	}

	public ScaleEffect(LImage t, boolean f) {
		this.image = t;
		this.width = t.getWidth();
		this.height = t.getHeight();
		this.timer = new LTimer(100);
		this.visible = true;
		if (f) {
			this.count = maxcount;
			this.flag = f;
		}
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
		if (flag) {
			if (this.count <= 0) {
				this.complete = true;
			}
			if (timer.action(elapsedTime)) {
				count--;
			}
		} else {
			if (this.count >= this.maxcount) {
				this.complete = true;
			}
			if (timer.action(elapsedTime)) {
				count++;
			}
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (complete) {
			if (!flag) {
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(alpha);
				}
				g.drawImage(this.image, x(), y(), width, height);
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(1f);
				}
			}
			return;
		}
		if (this.centerX < 0) {
			this.centerX = (width / 2);
		}
		if (this.centerY < 0) {
			this.centerY = (height / 2);
		}
		final float partx = this.centerX / this.maxcount;
		final float party = this.centerY / this.maxcount;
		final float partWidth = (width - this.centerX) / this.maxcount;
		final float partHeight = (height - this.centerY) / this.maxcount;
		final int x = (int) (this.centerX - this.count * partx) + x();
		final int y = (int) (this.centerY - this.count * party) + y();
		final int width = (int) (this.centerX + this.count * partWidth);
		final int height = (int) (this.centerY + this.count * partHeight);
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		g.drawImage(this.image, x, y, width, height);
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public void reset() {
		this.complete = false;
		this.count = 0;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	public LImage getBitmap() {
		return image;
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
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

}
