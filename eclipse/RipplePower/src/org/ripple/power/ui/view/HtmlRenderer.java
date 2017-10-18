package org.ripple.power.ui.view;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.ripple.power.txns.OfferPrice.OfferFruit;
import org.ripple.power.ui.graphics.LColor;

public class HtmlRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Border noFocusBorder = new EmptyBorder(5, 1, 1, 1) {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(LColor.gray);
			g.drawRect(x + 1, y + 1, width - 3, height - 3);
		}
	};

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setBorder(noFocusBorder);
		if (value instanceof OfferFruit) {
			OfferFruit fruit = (OfferFruit) value;
			this.setText("<html><b><i>" + fruit.message + "</i></b></html>");
		} else {
			String message = (String) value;
			this.setText("<html><b><i>" + message + "</i></b></html>");
		}
		return this;
	}

}