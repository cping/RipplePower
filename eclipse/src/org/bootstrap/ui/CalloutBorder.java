package org.bootstrap.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

public class CalloutBorder extends AbstractBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color borderColor = Color.decode("#EEE");
	private int borderWidth = 2;

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public CalloutBorder(Color borderColor, int borderWidth) {
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		g2.setColor(this.borderColor);
		g2.fillRect(0, 0, borderWidth, height);
		
		g2.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = (int) borderWidth;
		return insets;
	}
}
