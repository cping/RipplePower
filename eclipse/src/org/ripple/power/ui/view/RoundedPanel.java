package org.ripple.power.ui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager2;
import java.awt.Stroke;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.GraphicsUtils;

public class RoundedPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int cornerRadius;

	public RoundedPanel(LayoutManager2 layout) {
		super(layout);
		setOpaque(false);
		this.cornerRadius = LSystem.COMPONENT_CORNER_RADIUS;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int width = getWidth();
		int height = getHeight();

		Graphics2D g2 = (Graphics2D) g;

		GraphicsUtils.setExcellentRenderingHints(g2);

		g2.setColor(Color.black);
		g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

		Stroke original = g2.getStroke();
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(0));
		g2.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
		g2.setStroke(original);

	}
}
