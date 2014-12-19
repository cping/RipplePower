package org.ripple.power.ui.projector.action.sprite;

import java.awt.Color;
import java.awt.Font;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.core.LObject;
import org.ripple.power.utils.GraphicsUtils;

public class Label extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Font font;

	private boolean visible;

	private int width, height;

	private LColor color;

	private float alpha;

	private String label;

	public Label(String label, int x, int y) {
		this(GraphicsUtils.getFont(12), label, x, y);
	}

	public Label(String label, String font, int type, int size, int x, int y) {
		this(GraphicsUtils.getFont(font, type, size), label, x, y);
	}

	public Label(Font font, String label, int x, int y) {
		this.font = font;
		this.label = label;
		this.color = LColor.black;
		this.visible = true;
		this.setLocation(x, y);
	}

	public void setFont(String fontName, int type, int size) {
		setFont(GraphicsUtils.getFont(fontName, type, size));
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void createUI(LGraphics g) {
		if (visible) {
			Font oldFont = g.getFont();
			Color oldColor = g.getColor();
			g.setFont(font);
			g.setColor(color);
			this.width = g.getFontMetrics().stringWidth(label);
			this.height = font.getSize();
			g.setAntiAlias(true);
			if (alpha > 0 && alpha <= 1.0) {
				g.setAlpha(alpha);
				g.drawString(label, x(), y());
				g.setAlpha(1.0F);
			} else {
				g.drawString(label, x(), y());
			}
			g.setAntiAlias(false);
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void update(long timer) {

	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(int label) {
		setLabel(String.valueOf(label));
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public LImage getBitmap() {
		return null;
	}

	public void dispose() {
		
	}

}
