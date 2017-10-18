package org.ripple.power.ui.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;

import org.bootstrap.ui.RoundRectBorder;

import net.miginfocom.swing.MigLayout;

public class RPAlert extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float borderWidth = 1;
	private int arc = 8;

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

	public RPAlert(String text) {
		setOpaque(false);

		setBackground(Color.decode("#D8EECC"));
		setForeground(Color.decode("#316527"));
		setBorder(new RoundRectBorder(Color.decode("#CEE6B5"), borderWidth, arc));

		setLayout(new MigLayout("gap 0,insets 0", "[2%][94%][2%]", "[grow]"));
		JLabel title = new JLabel(text);
		title.setForeground(getForeground());
		add(title, "cell 1 0, grow");
	}

	@Override
	public void paintComponent(Graphics graphics) {
		float width = getWidth();
		float height = getHeight();

		Graphics2D g2 = (Graphics2D) graphics.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		RoundRectangle2D rect = new RoundRectangle2D.Float(borderWidth / 2, borderWidth / 2, width - borderWidth,
				height - borderWidth, arc, arc);
		g2.setColor(this.getBackground());
		g2.fill(rect);
		g2.dispose();

		super.paintComponent(graphics);
	}
}
