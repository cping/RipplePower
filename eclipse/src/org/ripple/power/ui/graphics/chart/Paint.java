package org.ripple.power.ui.graphics.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;


class Paint  {
	private static int getCap(Cap cap) {
		switch (cap) {
			case BUTT:
				return BasicStroke.CAP_BUTT;
			case ROUND:
				return BasicStroke.CAP_ROUND;
			case SQUARE:
				return BasicStroke.CAP_SQUARE;
		}

		throw new IllegalArgumentException("unknown cap: " + cap);
	}

	private static String getFontName(FontFamily fontFamily) {
		switch (fontFamily) {
			case MONOSPACE:
				return Font.MONOSPACED;
			case DEFAULT:
				return null;
			case SANS_SERIF:
				return Font.SANS_SERIF;
			case SERIF:
				return Font.SERIF;
		}

		throw new IllegalArgumentException("unknown fontFamily: " + fontFamily);
	}

	private static int getFontStyle(FontStyle fontStyle) {
		switch (fontStyle) {
			case BOLD:
				return Font.BOLD;
			case BOLD_ITALIC:
				return Font.BOLD | Font.ITALIC;
			case ITALIC:
				return Font.ITALIC;
			case NORMAL:
				return Font.PLAIN;
		}

		throw new IllegalArgumentException("unknown fontStyle: " + fontStyle);
	}

	java.awt.Color color;
	Font font;
	Stroke stroke;
	Style style;
	TexturePaint texturePaint;
	private int cap;
	private String fontName;
	private int fontStyle;
	private float[] strokeDasharray;
	private float strokeWidth;
	private float textSize;

	Paint() {
		this.cap = getCap(Cap.ROUND);
		this.color = java.awt.Color.BLACK;
		this.style = Style.FILL;
	}

	
	public int getTextHeight(String text) {
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(this.font);
		return fontMetrics.getHeight();
	}

	
	public int getTextWidth(String text) {
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		FontMetrics fontMetrics = bufferedImage.getGraphics().getFontMetrics(this.font);
		return fontMetrics.stringWidth(text);
	}

	
	public boolean isTransparent() {
		return this.texturePaint == null && this.color.getAlpha() == 0;
	}

	
	public void setBitmapShader(Bitmap bitmap) {
		Rectangle rectangle = new Rectangle(0, 0, bitmap.getWidth(), bitmap.getHeight());
		this.texturePaint = new TexturePaint(JavaSEGraphicFactory.getBufferedImage(bitmap).getBufferedImage(), rectangle);
	}

	
	public void setColor(Color color) {
		this.color = color;
	}

	
	public void setColor(int color) {
		this.color = new java.awt.Color(color);
	}

	
	public void setDashPathEffect(float[] strokeDasharray) {
		this.strokeDasharray = strokeDasharray;
		createStroke();
	}

	
	public void setStrokeCap(Cap cap) {
		this.cap = getCap(cap);
		createStroke();
	}

	
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		createStroke();
	}

	
	public void setStyle(Style style) {
		this.style = style;
	}

	
	public void setTextAlign(Align align) {
	}

	
	public void setTextSize(float textSize) {
		this.textSize = textSize;
		createFont();
	}

	
	public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
		this.fontName = getFontName(fontFamily);
		this.fontStyle = getFontStyle(fontStyle);
		createFont();
	}

	private void createFont() {
		if (this.textSize > 0) {
			this.font = new Font(this.fontName, this.fontStyle, (int) this.textSize);
		} else {
			this.font = null;
		}
	}

	private void createStroke() {
		if (this.strokeWidth <= 0) {
			return;
		}
		this.stroke = new BasicStroke(this.strokeWidth, this.cap, BasicStroke.JOIN_ROUND, 0, this.strokeDasharray, 0);
	}
}
