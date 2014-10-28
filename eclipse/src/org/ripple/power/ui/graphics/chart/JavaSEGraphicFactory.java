package org.ripple.power.ui.graphics.chart;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.ripple.power.ui.graphics.LColor;

public final class JavaSEGraphicFactory {
	public static final JavaSEGraphicFactory INSTANCE = new JavaSEGraphicFactory();


	public static Canvas createGraphicContext(Graphics graphics) {
		return new Canvas((Graphics2D) graphics);
	}

	static AffineTransform getAffineTransform(Matrix matrix) {
		return  matrix.affineTransform;
	}

	static Paint getAwtPaint(Paint paint) {
		return paint;
	}

	static Path getAwtPath(Path path) {
		return  path;
	}

	static BufferedImage getBufferedImage(Bitmap bitmap) {
		return  bitmap.bufferedImage;
	}


	private JavaSEGraphicFactory() {
	
	}

	
	public Bitmap createBitmap(InputStream inputStream) throws IOException {
		return new Bitmap(inputStream);
	}

	
	public Bitmap createBitmap(int width, int height) {
		return new Bitmap(width, height);
	}

	
	public Canvas createCanvas() {
		return new Canvas();
	}

	
	public int createColor(LColor color) {
		return color.getARGB();
	}

	
	public int createColor(int alpha, int red, int green, int blue) {
		return new LColor(red, green, blue, alpha).getRGB();
	}

	
	public Matrix createMatrix() {
		return new Matrix();
	}

	
	public Paint createPaint() {
		return new Paint();
	}

	
	public Path createPath() {
		return new Path();
	}
}
