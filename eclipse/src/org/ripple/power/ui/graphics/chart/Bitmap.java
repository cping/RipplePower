package org.ripple.power.ui.graphics.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

class Bitmap {
	BufferedImage bufferedImage;

	Bitmap(InputStream inputStream) throws IOException {
		this.bufferedImage = ImageIO.read(inputStream);
	}

	Bitmap(int width, int height) {
		this.bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
	}

	public void compress(OutputStream outputStream) throws IOException {
		ImageIO.write(this.bufferedImage, "png", outputStream);
	}

	public void scaleTo(int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = resizedImage.createGraphics();
		graphics.setComposite(AlphaComposite.Src);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawImage(bufferedImage, 0, 0, width, height, null);
		graphics.dispose();
		this.bufferedImage = resizedImage;
	}

	public void setBackgroundColor(int color) {
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setColor(new Color(color, true));
		graphics.fillRect(0, 0, bufferedImage.getWidth(),
				bufferedImage.getHeight());
		graphics.dispose();
	}

	public int getHeight() {
		return this.bufferedImage.getHeight();
	}

	public int getWidth() {
		return this.bufferedImage.getWidth();
	}

	public void recycle() {
	}

	public boolean isValid() {
		return true;
	}
}
