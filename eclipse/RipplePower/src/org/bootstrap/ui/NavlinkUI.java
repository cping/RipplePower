package org.bootstrap.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import org.ripple.power.ui.RPNavlink;

public class NavlinkUI extends BasicButtonUI {
	protected Color selectColor = Color.decode("#EBEBEB");
	protected Color focusColor = Color.decode("#EBEBEB");
	protected Color disabledTextColor = Color.decode("#878787");

	private Rectangle viewRect = new Rectangle();
	private Rectangle textRect = new Rectangle();
	private Rectangle iconRect = new Rectangle();

	public void setSelectColor(Color selectColor) {
		this.selectColor = selectColor;
	}

	public Color getSelectColor() {
		return selectColor;
	}

	public Color getFocusColor() {
		return focusColor;
	}

	public void setFocusColor(Color focusColor) {
		this.focusColor = focusColor;
	}

	public Color getDisabledTextColor() {
		return disabledTextColor;
	}

	public void setDisabledTextColor(Color disabledTextColor) {
		this.disabledTextColor = disabledTextColor;
	}

	public void paint(Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		String text = layout(b, g.getFontMetrics(), b.getWidth(), b.getHeight());

		clearTextShiftOffset();

		if (model.isArmed() && model.isPressed()) {
			paintButtonPressed(g, b);
		} else if (b.isRolloverEnabled() && model.isRollover()) {
			paintButtonPressed(g, b);
		}

		if (b.getIcon() != null) {
			paintIcon(g, c, iconRect);
		}

		if (b.isFocusPainted() && b.isFocusOwner()) {
			paintFocus(g, b, viewRect, textRect, iconRect);
			if (iconRect != null && iconRect.width > 0 && iconRect.height > 0) {
				if (b.getIcon() != null) {
					paintIcon(g, c, iconRect);
				}
			}
		}

		if (text != null && !text.equals("")) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				v.paint(g2, textRect);
			} else {
				paintText(g2, b, textRect, text);
			}
		}
	}

	private String layout(AbstractButton b, FontMetrics fm, int width, int height) {
		Insets i = b.getInsets();
		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = width - (i.right + viewRect.x);
		viewRect.height = height - (i.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		return SwingUtilities.layoutCompoundLabel(b, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(),
				b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect,
				iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		if (b.isContentAreaFilled()) {
			if (b instanceof RPNavlink) {
				RPNavlink link = (RPNavlink) b;
				if (link.isLeftNode()) {
					float width = link.getWidth();
					float height = link.getHeight();
					int arc = link.getNavbar().getArc();

					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

					RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, width, height, link.getNavbar().getArc(),
							arc);
					g2.setColor(getSelectColor());
					g2.fill(rect);

					Rectangle2D rectFix = new Rectangle((int) width - arc, 0, (int) arc, (int) height);
					g2.fill(rectFix);

					g2.dispose();
				} else {
					Dimension size = b.getSize();
					g.setColor(getSelectColor());
					g.fillRect(0, 0, size.width, size.height);
				}
			}
		}
	}

	protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		FontMetrics fm = g.getFontMetrics();
		int mnemIndex = b.getDisplayedMnemonicIndex();

		if (model.isEnabled()) {
			g.setColor(b.getForeground());
		} else {
			g.setColor(getDisabledTextColor());
		}
		BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
	}

	public Dimension getPreferredSize(JComponent c) {
		AbstractButton b = (AbstractButton) c;
		Dimension d = BasicGraphicsUtils.getPreferredButtonSize(b, b.getIconTextGap());
		Insets margin = b.getMargin();
		d.setSize(d.getWidth() + margin.left + margin.right, d.getHeight() + margin.top + margin.bottom);
		return d;
	}

}
