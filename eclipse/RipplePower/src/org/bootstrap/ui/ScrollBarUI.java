package org.bootstrap.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollBarUI extends BasicScrollBarUI {
	private JButton pButton = new JButton() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(0, 0);
		}

	};

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2.setPaint(Color.decode("#FBFBFB"));
		g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width,
				trackBounds.height);
		g2.dispose();
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2.setPaint(Color.decode("#B6B6B6"));
		g2.fillRoundRect(thumbBounds.x + 4, thumbBounds.y + 4,
				thumbBounds.width - 8, thumbBounds.height - 8, 10, 10);
		g2.dispose();
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return pButton;
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return pButton;
	}
}
