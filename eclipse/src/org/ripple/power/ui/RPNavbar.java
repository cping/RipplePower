package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bootstrap.ui.RoundRectBorder;

import net.miginfocom.swing.MigLayout;

public class RPNavbar extends JPanel {

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

	private void setTitle(String title) {
		this.title = title;

		if (this.titleLabel == null) {
			this.titleLabel = new JLabel(title);
			this.titleLabel.setForeground(getForeground());
			this.titleLabel.setFont(getFont().deriveFont(22f));

			this.titleLabel.addMouseListener(new MouseListener() {
				private Color colorSave;
				private int cursorSave;

				public void mouseEntered(MouseEvent e) {
					colorSave = titleLabel.getForeground();
					cursorSave = titleLabel.getCursor().getType();
					titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					titleLabel.setForeground(titleLabel.getForeground().darker());
				}

				public void mouseExited(MouseEvent e) {
					titleLabel.setForeground(colorSave);
					titleLabel.setCursor(Cursor.getPredefinedCursor(cursorSave));
				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				public void mouseClicked(MouseEvent e) {
				}
			});

			this.add(titleLabel, "gapright 18, grow");
		}
	}

	public RPNavbar() {
		setOpaque(false);

		setBackground(Color.decode("#F6F6F6"));
		setForeground(Color.decode("#5E5E5E"));
		setBorder(new RoundRectBorder(Color.decode("#E1E1E1"), borderWidth, arc));

		setLayout(new MigLayout("gap 0,insets 0", "[10%][8%][8%][8%][8%][8%][8%][8%][8%][8%][8%][fill]", "[grow]"));
	}

	public RPNavbar(String title) {
		this();

		setTitle(title);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		float width = getWidth();
		float height = getHeight();

		Graphics2D g2 = (Graphics2D) graphics.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		if (getBorder() != null) {
			RoundRectangle2D rect = new RoundRectangle2D.Float(borderWidth / 2, borderWidth / 2, width - borderWidth,
					height - borderWidth, arc, arc);
			g2.setColor(this.getBackground());
			g2.fill(rect);
		} else {
			Rectangle2D rect = new Rectangle2D.Float(0, 0, width, height);
			g2.setColor(this.getBackground());
			g2.fill(rect);
		}
		g2.dispose();

	}

	public int setNavLinkList(List<JComponent> navLinkList) {
		for (JComponent comp : navLinkList) {
			if (comp instanceof RPNavlink) {
				RPNavlink linkItem = (RPNavlink) comp;
				linkItem.setNavbar(this);
				linkItem.setForeground(this.getForeground());
				linkItem.setFocusable(this.linkFocusable);
				if (linkItem.getNavigationAlignment() == RPNavlink.ALIGN_LEFT) {
					this.leftLinkList.add(linkItem);
				} else if (linkItem.getNavigationAlignment() == RPNavlink.ALIGN_RIGHT) {
					this.rightLinkList.add(linkItem);
				}
			}
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
		if (span > 0) {
			this.add(new JLabel(), "span " + span + ",grow");
		}
		for (i = 0; i < rightLinkList.size(); ++i) {
			JComponent comp = rightLinkList.get(i);
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
		if (this.titleLabel == null) {
			JComponent comp = leftLinkList.get(0);
			if (comp instanceof RPNavlink) {
				((RPNavlink) comp).setLeftNode(true);
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
