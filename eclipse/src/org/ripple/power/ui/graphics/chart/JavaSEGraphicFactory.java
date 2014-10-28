package org.ripple.power.ui.graphics.chart;

import java.awt.geom.AffineTransform;


import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;

public final class JavaSEGraphicFactory {
	public static final JavaSEGraphicFactory INSTANCE = new JavaSEGraphicFactory();


	public static Canvas createGraphicContext(LGraphics graphics) {
		return new Canvas(graphics);
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

	static LImage getBufferedImage(Bitmap bitmap) {
		return  bitmap.bufferedImage;
	}


	private JavaSEGraphicFactory() {
	
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
