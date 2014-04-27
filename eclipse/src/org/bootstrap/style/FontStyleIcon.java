package org.bootstrap.style;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

public class FontStyleIcon implements Icon {

	private FontStyle.Icon icon;

	private int iconSize;

	private Color color;

	private Font font;

	private BufferedImage imageBuffer = null;

	private int iconWidth;
	private int iconHeight;
	private int iconBaseline;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;

		paintImageBuffer();
	}

	public FontStyle.Icon getIcon() {
		return icon;
	}

	public void setIcon(FontStyle.Icon icon) {
		this.icon = icon;
	}

	public int getIconSize() {
		return iconSize;
	}

	public void setIconSize(int iconSize) {
		this.iconSize = iconSize;
	}

	public Image getImage() {
		return imageBuffer;
	}

	public FontStyleIcon(FontStyle.Icon icon, int iconSize) {
		this(icon, iconSize, Color.white);
	}

	public FontStyleIcon(FontStyle.Icon icon, int iconSize, Color color) {
		this.icon = icon;
		this.iconSize = iconSize;
		this.color = color;
		this.font = FontStyle.getInstance().getFont().deriveFont(Font.PLAIN, iconSize);

		calcImageBufferSize();
		paintImageBuffer();
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (icon == null || imageBuffer == null) {
			return;
		}

		g.drawImage(imageBuffer, x, y, null);
	}

	private void calcImageBufferSize() {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		BufferedImage buff = config.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
		Graphics2D g2 = buff.createGraphics();
		g2.setFont(font);

		LineMetrics lm = font.getLineMetrics(String.valueOf(icon), g2.getFontRenderContext());
		iconWidth = g2.getFontMetrics().charWidth(icon.getChar());
		iconHeight = (int) lm.getHeight();
		iconBaseline = (int) (lm.getHeight() - lm.getLeading() - lm.getDescent());
		g2.dispose();
	}

	private void paintImageBuffer() {
		if (imageBuffer == null) {	
			GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			imageBuffer = config.createCompatibleImage(iconWidth, iconHeight, Transparency.TRANSLUCENT);
		} else { 
			int pixelSize = imageBuffer.getWidth() * imageBuffer.getHeight() * 4;
			imageBuffer.getRaster().setPixels(0, 0, iconWidth, iconHeight, new int[pixelSize]);
		}
		
		Graphics2D g2 = imageBuffer.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setFont(font);
		g2.setColor(color);
		g2.drawString(String.valueOf(icon), 0, iconBaseline);
		g2.dispose();
	}

}
