package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

import org.bootstrap.ui.ButtonUI;
import org.ripple.power.utils.GraphicsUtils;


public class RPButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Color selectColor = Color.decode("#EBEBEB");
	protected Color focusColor = Color.decode("#EBEBEB");
	protected Color disabledTextColor = Color.decode("#878787");

	private boolean isLeftNode = false;
	private boolean isRightNode = false;

	private RPButtonGroup btnGroup = null;

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

	public boolean isLeftNode() {
		return isLeftNode;
	}

	public void setLeftNode(boolean isLeftNode) {
		this.isLeftNode = isLeftNode;
	}

	public boolean isRightNode() {
		return isRightNode;
	}

	public void setRightNode(boolean isRightNode) {
		this.isRightNode = isRightNode;
	}

	public RPButtonGroup getBtnGroup() {
		return btnGroup;
	}

	public void setBtnGroup(RPButtonGroup btnGroup) {
		this.btnGroup = btnGroup;
	}

	public RPButton() {
		this(null, null);
	}

	public RPButton(Action a) {
		this();
		setAction(a);
	}

	public RPButton(Icon icon) {
		this(null, icon);
	}

	public RPButton(String text) {
		this(text, null);
	}

	public RPButton(String text, Icon icon) {
		super(text, icon);

		setOpaque(false);

		setBackground(Color.WHITE);

		setFont(getFont().deriveFont(16f));

		setBorder(BorderFactory.createEmptyBorder());

		if (text == null) {
			setMargin(new Insets(0, 10, 0, 10));
		}

		ButtonUI navUI = new ButtonUI();
		setUI(navUI);

	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (g != null) {
			g2d.setRenderingHints(GraphicsUtils.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHints(GraphicsUtils.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		super.paintComponent(g2d);
		g2d.dispose();
	}
}
