package org.ripple.power.ui.projector.action.sprite;

import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;

public class SpriteSheet {

	private int margin, spacing;

	private int tw, th;

	private int width, height;

	private LImage[][] subImages;

	private LImage target;

	public SpriteSheet(String fileName, int tw, int th, int s, int m) {
		this(LImage.createImage(fileName), tw, th, s, m);
	}

	public SpriteSheet(String fileName, int tw, int th) {
		this(LImage.createImage(fileName), tw, th, 0, 0);
	}

	public SpriteSheet(LImage image, int tw, int th) {
		this(image, tw, th, 0, 0);
	}

	public SpriteSheet(LImage image, int tw, int th, int s, int m) {
		this.target = image;
		this.tw = tw;
		this.th = th;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.margin = m;
		this.spacing = s;
	}

	public void update() {
		if (subImages != null) {
			return;
		}
		int tilesAcross = ((width - (margin * 2) - tw) / (tw + spacing)) + 1;
		int tilesDown = ((height - (margin * 2) - th) / (th + spacing)) + 1;
		if ((height - th) % (th + spacing) != 0) {
			tilesDown++;
		}

		subImages = new LImage[tilesAcross][tilesDown];
		for (int x = 0; x < tilesAcross; x++) {
			for (int y = 0; y < tilesDown; y++) {
				subImages[x][y] = getImage(x, y);
			}
		}
	}

	public LImage[][] getSubImages() {
		return subImages;
	}

	private void checkImage(int x, int y) {
		update();
		if ((x < 0) || (x >= subImages.length)) {
			throw new RuntimeException("SubImage out of sheet bounds " + x
					+ "," + y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new RuntimeException("SubImage out of sheet bounds " + x
					+ "," + y);
		}
	}

	private LImage getImage(int x, int y) {
		checkImage(x, y);
		return target.getSubImage(x * (tw + spacing) + margin, y
				* (th + spacing) + margin, tw, th);
	}

	public int getHorizontalCount() {
		update();
		return subImages.length;
	}

	public int getVerticalCount() {
		update();
		return subImages[0].length;
	}

	public LImage getSubImage(int x, int y) {
		checkImage(x, y);
		return subImages[x][y];
	}

	public Sprite getSprite(int x, int y) {
		return new Sprite(getSubImage(x, y));
	}

	public void draw(LGraphics g, int x, int y, int sx, int sy) {
		checkImage(sx, sy);
		g.drawImage(subImages[sx][sy], x, y);
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public int getSpacing() {
		return spacing;
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public LImage getTarget() {
		return target;
	}

	public void setTarget(LImage target) {
		this.target = target;
	}
}
