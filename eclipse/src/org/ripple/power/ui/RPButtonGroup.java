package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bootstrap.ui.RoundRectBorder;

import net.miginfocom.swing.MigLayout;

public class RPButtonGroup extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float borderWidth = 1;
	private int arc = 8;
	private String title;
	private JLabel titleLabel;

	private List<JComponent> leftLinkList = new ArrayList<JComponent>();
	private List<JComponent> rightLinkList = new ArrayList<JComponent>();
	private boolean linkFocusable = true;

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

	public String getTitle() {
		return title;
	}

	public RPButtonGroup() {
		setOpaque(false);

		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.BLACK);
		setBorder(new RoundRectBorder(Color.LIGHT_GRAY, borderWidth, arc));

		setLayout(new MigLayout("gap 1,insets 0", "[sg sg1]", "[grow]"));
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		float width = getWidth();
		float height = getHeight();

		Graphics2D g2 = (Graphics2D) graphics.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		if (getBorder() != null) {
			RoundRectangle2D rect = new RoundRectangle2D.Float(borderWidth / 2,
					borderWidth / 2, width - borderWidth, height - borderWidth,
					arc, arc);
			g2.setColor(this.getBackground());
			g2.fill(rect);
		} else {
			Rectangle2D rect = new Rectangle2D.Float(0, 0, width, height);
			g2.setColor(this.getBackground());
			g2.fill(rect);
		}
		g2.dispose();

	}

	public int setButtons(List<RPButton> navLinkList) {
		for (RPButton btn : navLinkList) {
			btn.setBtnGroup(this);
			btn.setForeground(this.getForeground());
			btn.setFocusable(this.linkFocusable);
			leftLinkList.add(btn);
			add(btn, "grow");
		}

		setLeftNode();

		int span = 12 - this.leftLinkList.size() - this.rightLinkList.size();
		if (this.titleLabel != null) {
			span = span - 1;
		}
		if (span < 0) {
			span = 0;
		}

		int i = 0;
		for (i = 0; i < leftLinkList.size(); ++i) {
			JComponent comp = leftLinkList.get(i);
			this.add(comp, "grow");
		}
		return 0;
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);

		if (this.titleLabel != null) {
			this.titleLabel.setForeground(fg);
		}
	}

	private void setLeftNode() {
		if (leftLinkList.size() > 0) {
			JComponent comp = leftLinkList.get(0);
			if (comp instanceof RPButton) {
				((RPButton) comp).setLeftNode(true);
			}

			JComponent lastComp = leftLinkList.get(leftLinkList.size() - 1);
			if (lastComp instanceof RPButton) {
				((RPButton) lastComp).setRightNode(true);
			}
		}
	}

	public void setNavLinkFocusable(boolean focusable) {
		this.linkFocusable = focusable;

		for (JComponent comp : leftLinkList) {
			comp.setFocusable(focusable);
		}
		for (JComponent comp : rightLinkList) {
			comp.setFocusable(focusable);
		}
	}

}
