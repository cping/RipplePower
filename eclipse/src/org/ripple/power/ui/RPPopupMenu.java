package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPopupMenu;
import javax.swing.border.Border;

public class RPPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class RoundedBorder implements Border {
		private int radius;
		private Color color;

		public RoundedBorder(int radius, Color color) {
			this.radius = radius;
			this.color = color;
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(this.radius + 1, this.radius + 1,
					this.radius + 2, this.radius);
		}

		public boolean isBorderOpaque() {
			return true;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(color);
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	protected Color BORDER_GRAY = new Color(119, 119, 119);

	public RPPopupMenu() {
		this.setBorder(new RoundedBorder(10, BORDER_GRAY));
		this.setBackground(new Color(245, 245, 245));
	}
}