package org.ripple.power.ui.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.ripple.power.helper.Gradation;
import org.ripple.power.ui.graphics.LColor;

public class RPPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class RoundedBorder implements Border {
		private int radius;
		private LColor color;

		public RoundedBorder(int radius, LColor color) {
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
			g.setColor(LColor.black);
			g.drawRoundRect(x, y, width - 2, height - 2, radius, radius);
		}
	}

	private Gradation _gradation;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (_gradation == null) {
			_gradation = Gradation.getInstance(LColor.WHITE, LColor.BLACK,
					getWidth(), getHeight(), 255);
		}
		_gradation.drawHeight(g, 0, 0);

	}

	protected LColor BORDER_GRAY = new LColor(119, 119, 119);

	public RPPopupMenu() {
		this.setBorder(new RoundedBorder(10, BORDER_GRAY));
		this.setBackground(new LColor(245, 245, 245));
		this.setOpaque(true);
	}
}