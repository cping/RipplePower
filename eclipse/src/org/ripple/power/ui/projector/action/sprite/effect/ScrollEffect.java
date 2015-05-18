package org.ripple.power.ui.projector.action.sprite.effect;

import org.ripple.power.config.LSystem;
import org.ripple.power.timer.LTimer;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.action.map.Config;
import org.ripple.power.ui.projector.action.sprite.ISprite;
import org.ripple.power.ui.projector.core.LObject;

public class ScrollEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int backgroundLoop;

	private int count;

	private int width, height;

	private float alpha;

	private LImage image;

	private boolean visible, stop;

	private LTimer timer;

	private int code;

	public ScrollEffect(String fileName) {
		this(new LImage(fileName));
	}

	public ScrollEffect(LImage tex2d) {
		this(Config.DOWN, tex2d, LSystem.screenRect);
	}

	public ScrollEffect(int d, String fileName) {
		this(d, new LImage(fileName));
	}

	public ScrollEffect(int d, LImage tex2d) {
		this(d, tex2d, LSystem.screenRect);
	}

	public ScrollEffect(int d, String fileName, RectBox limit) {
		this(d, new LImage(fileName), limit);
	}

	public ScrollEffect(int d, LImage tex2d, RectBox limit) {
		this(d, tex2d, limit.x, limit.y, limit.width, limit.height);
	}

	public ScrollEffect(int d, LImage tex2d, float x, float y, int w, int h) {
		this.setLocation(x, y);
		this.image = tex2d;
		this.width = w;
		this.height = h;
		this.count = 1;
		this.timer = new LTimer(10);
		this.visible = true;
		this.code = d;
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
		if (stop) {
			return;
		}
		if (timer.action(elapsedTime)) {
			switch (code) {
			case Config.DOWN:
			case Config.TDOWN:
			case Config.UP:
			case Config.TUP:
				this.backgroundLoop = ((backgroundLoop + count) % height);
				break;
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TLEFT:
			case Config.TRIGHT:
				this.backgroundLoop = ((backgroundLoop + count) % width);
				break;
			}
		}
	}

	public void createUI(LGraphics g) {
		if (!visible) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		switch (code) {
		case Config.DOWN:
		case Config.TDOWN:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.drawImage(image, x() + (j * width), y()
							+ (i * height + backgroundLoop), width, height, 0,
							0, width, height);
				}
			}
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.drawImage(image, x() + (j * width + backgroundLoop), y()
							+ (i * height), width, height, 0, 0, width, height);
				}
			}
			break;
		case Config.UP:
		case Config.TUP:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.drawImage(image, x() + (j * width), y()
							- (i * height + backgroundLoop), width, height, 0,
							0, width, height);
				}
			}
			break;
		case Config.LEFT:
		case Config.TLEFT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.drawImage(image, x() - (j * width + backgroundLoop), y()
							+ (i * height), width, height, 0, 0, width, height);
				}
			}
			break;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public LImage getBitmap() {
		return image;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
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
