package org.ripple.power.ui.projector.action.sprite;

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.core.LObject;

public class Picture extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982153514439690901L;

	private boolean visible;

	private float alpha;

	private int width, height;

	private LImage image;

	public Picture(String fileName) {
		this(fileName, 0, 0);
	}

	public Picture(int x, int y) {
		this((LImage) null, x, y);
	}

	public Picture(String fileName, int x, int y) {
		this(LImage.createImage(fileName), x, y);
	}

	public Picture(LImage image) {
		this(image, 0, 0);
	}

	public Picture(LImage image, int x, int y) {
		if (image != null) {
			this.setImage(image);
			this.width = image.getWidth();
			this.height = image.getHeight();
		}
		this.setLocation(x, y);
		this.visible = true;
	}

	public void createUI(LGraphics g) {
		if (visible) {
			if (alpha >= 0.1 && alpha <= 1.0) {
				g.setAlpha(alpha);
			}
			g.drawImage(image, x(), y());
			if (alpha != 0) {
				g.setAlpha(1.0f);
			}
		}
	}

	public boolean equals(Picture p) {
		if (this.width == p.width && this.height == p.height) {
			if (image.hashCode() == p.image.hashCode()) {
				return true;
			}
		}
		return false;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long timer) {
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

	public LImage getImage() {
		return image;
	}

	public void setImage(LImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public void setImage(String fileName) {
		setImage(LImage.createImage(fileName));
	}

	public void setImage(Image image) {
		setImage(new LImage(image));
	}

	public void setImage(BufferedImage image) {
		this.image = new LImage(image);
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public LImage getBitmap() {
		return image;
	}

}
