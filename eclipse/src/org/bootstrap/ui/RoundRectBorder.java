package org.bootstrap.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

public class RoundRectBorder extends AbstractBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color borderColor = Color.decode("#E1E1E1");
	private float borderWidth = 1;
	private int arc = 8;

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getArc() {
		return arc;
	}

	public void setArc(int arc) {
		this.arc = arc;
	}

	public RoundRectBorder(Color borderColor, float borderWidth, int arc) {
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		this.arc = arc;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		RoundRectangle2D rect = new RoundRectangle2D.Float(borderWidth / 2, borderWidth / 2, width - borderWidth,
				height - borderWidth, arc, arc);
		g2.setStroke(new BasicStroke(this.borderWidth));
		g2.setColor(this.borderColor);
		g2.draw(rect);
		g2.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.right = (int) borderWidth;
		insets.top = insets.bottom = (int) borderWidth;
		return insets;
	}
}
