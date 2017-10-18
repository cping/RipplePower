package org.ripple.power.ui.projector.action.sprite.effect;

import org.ripple.power.timer.LTimer;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class CrossEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float alpha;

	private int width, height;

	private boolean visible, complete;

	private LImage oimage, nimage;

	private LTimer timer;

	private int count, code;

	private int maxcount = 16;

	private int part;

	private int left;

	private int right;

	private LImage tmp;

	public CrossEffect(int c, String fileName) {
		this(c, new LImage(fileName));
	}

	public CrossEffect(int c, String file1, String file2) {
		this(c, new LImage(file1), new LImage(file2));
	}

	public CrossEffect(int c, LImage o) {
		this(c, o, null);
	}

	public CrossEffect(int c, LImage o, LImage n) {
		this.code = c;
		this.oimage = o;
		this.nimage = n;
		this.width = o.getWidth();
		this.height = o.getHeight();
		if (width > height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.timer = new LTimer(160);
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
		if (this.count > this.maxcount) {
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
			if (nimage != null) {
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(alpha);
				}
				g.drawImage(nimage, x(), y());
				if (alpha > 0 && alpha < 1) {
					g.setAlpha(1f);
				}
			}
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		part = 0;
		left = 0;
		right = 0;
		tmp = null;
		switch (code) {
		default:
			part = width / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.nimage;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.oimage;
				}

				left = i * 2 * part;
				right = width - ((i + 1) * 2 - 1) * part;
				g.drawImage(tmp, x() + left, y(), part, height, left, 0, left + part, height);
				g.drawImage(tmp, x() + right, y(), part, height, right, 0, right + part, height);

			}
			break;
		case 1:
			part = height / this.maxcount / 2;
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.nimage;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.oimage;
				}
				int up = i * 2 * part;
				int down = height - ((i + 1) * 2 - 1) * part;

				g.drawImage(tmp, 0, up, width, part, 0, up, width, up + part);
				g.drawImage(tmp, 0, down, width, part, 0, down, width, down + part);

			}
			break;
		}
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

	public LImage getBitmap() {
		return oimage;
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

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	public void dispose() {
		if (oimage != null) {
			oimage.dispose();
			oimage = null;
		}
		if (nimage != null) {
			nimage.dispose();
			nimage = null;
		}
	}

}
