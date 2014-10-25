package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.ripple.power.helper.Gradation;
import org.ripple.power.utils.LColor;

public class RPCScrollPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Gradation _gradation;

	private LColor _colorStart, _colorEnd;

	private int _alpha;

	public RPCScrollPane(Component view, LColor start, LColor end, int alpha) {
		super(view);
		_colorStart = start;
		_colorEnd = end;
		_alpha = alpha;
		setOpaque(false);
		setBackground(new LColor(0f, 0F, 0F, 0F));
		setBorder(new EmptyBorder(1, 1, 1, 1));
		getViewport().setOpaque(false);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (_gradation == null) {
			_gradation = Gradation.getInstance(_colorStart, _colorEnd,
					getWidth(), getHeight(), _alpha);
		}
		_gradation.drawHeight(g, 0, 0);
		g.setColor(LColor.white.brighter());
		g.drawRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
		g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 0, 0);
	}
}