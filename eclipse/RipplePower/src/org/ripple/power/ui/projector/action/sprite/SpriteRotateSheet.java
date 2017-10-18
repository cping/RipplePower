package org.ripple.power.ui.projector.action.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.MathUtils;

public class SpriteRotateSheet {

	private final int width, height, halfWidth, halfHeight;

	private int number, bitmapWidth, bitmapHeight;

	private boolean isCircle;

	private BufferedImage sheetRotationImages;

	public SpriteRotateSheet(String fileName, int number, boolean c) {
		this(GraphicsUtils.loadImage(fileName), number, c);
	}

	public SpriteRotateSheet(Image img, int number, boolean c) {
		this.isCircle = c;
		this.number = number;
		this.width = img.getWidth(null);
		this.height = img.getHeight(null);
		if (!suited(width, height)) {
			throw new RuntimeException("size not allowed :" + width + "," + height);
		}
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.bitmapWidth = width;
		this.bitmapHeight = height;
		Graphics2D g = null;
		if (c) {
			this.sheetRotationImages = GraphicsUtils.createImage(width * number + width, height, true);
			g = sheetRotationImages.createGraphics();
			int x;
			for (int i = 0; i < number; i++) {
				x = i * width;
				double degrees = Math.toRadians(i * 360 / number);
				g.setTransform(AffineTransform.getRotateInstance(degrees, x + halfWidth, halfHeight));
				g.drawImage(img, x, 0, null);
			}
		} else {
			RectBox[] lazyRotates = new RectBox[360];
			for (int i = 0; i < number; i++) {
				int index = i * 360 / number;
				RectBox rect = MathUtils.getBounds(0, 0, width, height, index);
				lazyRotates[i] = rect;
				bitmapWidth = Math.max(bitmapWidth, rect.width);
				bitmapHeight = Math.max(bitmapHeight, rect.height);
			}
			this.sheetRotationImages = GraphicsUtils.createImage(bitmapWidth * number + bitmapWidth, bitmapHeight,
					true);
			g = sheetRotationImages.createGraphics();
			int x = 0;
			for (int i = 0; i < number; i++) {
				double degrees = Math.toRadians(i * 360 / number);
				RectBox rect = lazyRotates[i];
				x = (i * (bitmapWidth));
				g.setTransform(AffineTransform.getRotateInstance(degrees, x + rect.width / 2, rect.height / 2));
				g.drawImage(img, x + (rect.width - width) / 2, (rect.height - height) / 2, null);
			}
			lazyRotates = null;
		}
		if (g != null) {
			g.dispose();
			g = null;
		}
	}

	public static boolean suited(int w, int h) {
		return (w == h || (w > 16 && w < 64 && h > 16 && h < 64)) && (w <= 256 && h <= 256);
	}

	public void draw(LGraphics g, int x, int y, double rotation) {
		if (sheetRotationImages != null) {
			synchronized (sheetRotationImages) {
				while (rotation < 0) {
					rotation += 360;
				}
				while (rotation > 360) {
					rotation -= 360;
				}
				int spriteIndex = (int) (rotation * number / 360);
				if (isCircle) {
					x = (int) x - halfWidth;
					y = (int) y - halfHeight;
				} else {
					double rotate = Math.toRadians(rotation);
					double sinA = Math.sin(rotate);
					double cosA = Math.cos(rotate);
					x = (int) (x - (halfWidth - (halfWidth * cosA - halfHeight * sinA)));
					y = (int) (y - (halfHeight - (halfHeight * cosA + halfWidth * sinA)));
				}
				g.drawImage(sheetRotationImages, x, y, x + bitmapWidth, y + bitmapHeight, spriteIndex * bitmapWidth, 0,
						(spriteIndex * bitmapWidth + bitmapWidth), bitmapHeight);
			}
		}
	}

	public void dispose() {
		if (sheetRotationImages != null) {
			synchronized (sheetRotationImages) {
				sheetRotationImages.flush();
				sheetRotationImages = null;
			}
		}
	}

	public BufferedImage getSheetImage() {
		return sheetRotationImages;
	}

	public int getHeight() {
		return height;
	}

	public int getNumber() {
		return number;
	}

	public int getWidth() {
		return width;
	}

}
