package org.ripple.power.ui.graphics;

import java.awt.Color;

public class LColor extends Color {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final static LColor silver = new LColor(0xffc0c0c0);

	public final static LColor lightBlue = new LColor(0xffadd8e6);

	public final static LColor lightCoral = new LColor(0xfff08080);

	public final static LColor lightCyan = new LColor(0xffe0ffff);

	public final static LColor lightGoldenrodYellow = new LColor(0xfffafad2);

	public final static LColor lightGreen = new LColor(0xff90ee90);

	public final static LColor lightPink = new LColor(0xffffb6c1);

	public final static LColor lightSalmon = new LColor(0xffffa07a);

	public final static LColor lightSeaGreen = new LColor(0xff20b2aa);

	public final static LColor lightSkyBlue = new LColor(0xff87cefa);

	public final static LColor lightSlateGray = new LColor(0xff778899);

	public final static LColor lightSteelBlue = new LColor(0xffb0c4de);

	public final static LColor lightYellow = new LColor(0xffffffe0);

	public final static LColor lime = new LColor(0xff00ff00);

	public final static LColor limeGreen = new LColor(0xff32cd32);

	public final static LColor linen = new LColor(0xfffaf0e6);

	public final static LColor maroon = new LColor(0xff800000);

	public final static LColor mediumAquamarine = new LColor(0xff66cdaa);

	public final static LColor mediumBlue = new LColor(0xff0000cd);

	public final static LColor purple = new LColor(0xff800080);

	public final static LColor wheat = new LColor(0xfff5deb3);

	public final static LColor gold = new LColor(0xffffd700);

	public static final LColor white = new LColor(1.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor yellow = new LColor(1.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor red = new LColor(1.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor blue = new LColor(0.0f, 0.0f, 1.0f, 1.0f);

	public static final LColor cornFlowerBlue = new LColor(0.4f, 0.6f, 0.9f, 1.0f);

	public static final LColor green = new LColor(0.0f, 1.0f, 0.0f, 1.0f);

	public static final LColor black = new LColor(0.0f, 0.0f, 0.0f, 1.0f);

	public static final LColor gray = new LColor(0.5f, 0.5f, 0.5f, 1.0f);

	public static final LColor cyan = new LColor(0.0f, 1.0f, 1.0f, 1.0f);

	public static final LColor darkGray = new LColor(0.3f, 0.3f, 0.3f, 1.0f);

	public static final LColor lightGray = new LColor(0.7f, 0.7f, 0.7f, 1.0f);

	public final static LColor pink = new LColor(1.0f, 0.7f, 0.7f, 1.0f);

	public final static LColor orange = new LColor(1.0f, 0.8f, 0.0f, 1.0f);

	public final static LColor magenta = new LColor(1.0f, 0.0f, 1.0f, 1.0f);

	public final static int transparent = 0xff000000;

	public LColor(Color c) {
		super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public LColor(int r, int g, int b) {
		super(r, g, b, 255);
	}

	public LColor(int r, int g, int b, int a) {
		super(r, g, b, a);
	}

	public LColor(int rgb) {
		super(rgb);
	}

	public LColor(int rgba, boolean alpha) {
		super(rgba, alpha);
	}

	public LColor(float r, float g, float b) {
		super(r, g, b);
	}

	public LColor(float r, float g, float b, float a) {
		super(r, g, b, a);
	}

	/**
	 * 返回ARGB
	 * 
	 * @return
	 */
	public int getARGB() {
		return getARGB(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * 获得24位色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int getRGB(int r, int g, int b) {
		return getARGB(r, g, b, 0xff);
	}

	/**
	 * 获得RGB颜色
	 * 
	 * @param pixels
	 * @return
	 */
	public static int getRGB(int pixels) {
		int r = (pixels >> 16) & 0xff;
		int g = (pixels >> 8) & 0xff;
		int b = pixels & 0xff;
		return getRGB(r, g, b);
	}

	/**
	 * 获得32位色
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 * @return
	 */
	public static int getARGB(int r, int g, int b, int alpha) {
		return (alpha << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * 获得Aplha
	 * 
	 * @param color
	 * @return
	 */
	public static int getAlpha(int color) {
		return color >>> 24;
	}

	/**
	 * 获得Red
	 * 
	 * @param color
	 * @return
	 */
	public static int getRed(int color) {
		return (color >> 16) & 0xff;
	}

	/**
	 * 获得Green
	 * 
	 * @param color
	 * @return
	 */
	public static int getGreen(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * 获得Blud
	 * 
	 * @param color
	 * @return
	 */
	public static int getBlue(int color) {
		return color & 0xff;
	}

	/**
	 * 像素前乘
	 * 
	 * @param argbColor
	 * @return
	 */
	public static int premultiply(int argbColor) {
		int a = argbColor >>> 24;
		if (a == 0) {
			return 0;
		} else if (a == 255) {
			return argbColor;
		} else {
			int r = (argbColor >> 16) & 0xff;
			int g = (argbColor >> 8) & 0xff;
			int b = argbColor & 0xff;
			r = (a * r + 127) / 255;
			g = (a * g + 127) / 255;
			b = (a * b + 127) / 255;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 像素前乘
	 * 
	 * @param rgbColor
	 * @param alpha
	 * @return
	 */
	public static int premultiply(int rgbColor, int alpha) {
		if (alpha <= 0) {
			return 0;
		} else if (alpha >= 255) {
			return 0xff000000 | rgbColor;
		} else {
			int r = (rgbColor >> 16) & 0xff;
			int g = (rgbColor >> 8) & 0xff;
			int b = rgbColor & 0xff;

			r = (alpha * r + 127) / 255;
			g = (alpha * g + 127) / 255;
			b = (alpha * b + 127) / 255;
			return (alpha << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 消除前乘像素
	 * 
	 * @param preARGBColor
	 * @return
	 */
	public static int unpremultiply(int preARGBColor) {
		int a = preARGBColor >>> 24;
		if (a == 0) {
			return 0;
		} else if (a == 255) {
			return preARGBColor;
		} else {
			int r = (preARGBColor >> 16) & 0xff;
			int g = (preARGBColor >> 8) & 0xff;
			int b = preARGBColor & 0xff;

			r = 255 * r / a;
			g = 255 * g / a;
			b = 255 * b / a;
			return (a << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 * 获得r,g,b
	 * 
	 * @param pixel
	 * @return
	 */
	public static int[] getRGBs(final int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel >> 16) & 0xff;
		rgbs[1] = (pixel >> 8) & 0xff;
		rgbs[2] = (pixel) & 0xff;
		return rgbs;
	}

	public Color getAWTColor() {
		return new Color(getRed(), getGreen(), getBlue(), getAlpha());
	}

}
