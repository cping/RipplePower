package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JToolBar;
import javax.swing.plaf.metal.MetalBorders.ToolBarBorder;

public class EmptyToolBarBorder extends ToolBarBorder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Insets getBorderInsets(Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component c, Insets newInsets) {
		newInsets.top = newInsets.left = newInsets.bottom = newInsets.right = 2;
		if (!(c instanceof JToolBar))
			return newInsets;
		if (((JToolBar) c).isFloatable()) {
			if (((JToolBar) c).getOrientation() == HORIZONTAL) {
				if (c.getComponentOrientation().isLeftToRight()) {
					newInsets.left = 8 + 2;
				} else {
					newInsets.right = 8 + 2;
				}
			} else {
				newInsets.top = 8 + 2;
			}
		}
		Insets margin = ((JToolBar) c).getMargin();
		if (margin != null) {
			newInsets.left += margin.left;
			newInsets.top += margin.top;
			newInsets.right += margin.right;
			newInsets.bottom += margin.bottom;
		}
		return newInsets;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {

	}

	protected void drawXPBorder(Component c, Graphics g, int x, int y, int w,
			int h) {

	}
}
