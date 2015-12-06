package org.ripple.power.ui.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class Graphics2DStore {

	private Paint paint;

	private Font font;

	private Stroke stroke;

	private AffineTransform transform;

	private Composite composite;

	private Shape clip;

	private RenderingHints renderingHints;

	private Color color;

	private Color background;

	public void save(Graphics2D g2d) {
		paint = g2d.getPaint();
		font = g2d.getFont();
		stroke = g2d.getStroke();
		transform = g2d.getTransform();
		composite = g2d.getComposite();
		clip = g2d.getClip();
		renderingHints = g2d.getRenderingHints();
		color = g2d.getColor();
		background = g2d.getBackground();
	}

	public void restore(Graphics2D g2d) {
		g2d.setPaint(paint);
		g2d.setFont(font);
		g2d.setStroke(stroke);
		g2d.setTransform(transform);
		g2d.setComposite(composite);
		g2d.setClip(clip);
		g2d.setRenderingHints(renderingHints);
		g2d.setColor(color);
		g2d.setBackground(background);
	}

	public Color getBackground() {
		return background;
	}

	public Shape getClip() {
		return clip;
	}

	public Color getColor() {
		return color;
	}

	public Composite getComposite() {
		return composite;
	}

	public Font getFont() {
		return font;
	}

	public Paint getPaint() {
		return paint;
	}

	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	public void setRenderingHints(RenderingHints renderingHints) {
		this.renderingHints = renderingHints;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public AffineTransform getTransform() {
		return transform;
	}

}
