package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bootstrap.ui.NavlinkUI;

public class RPNavlink extends JButton {

	public static interface Click {

		public void down();

		public void up();
		
		public void move();

		public void exit();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_RIGHT = 1;

	private RPNavbar navbar;
	private boolean isLeftNode = false;
	private int navigationAlignment = ALIGN_LEFT;

	private JLabel titleLabel;

	private JPanel containerPanel;
	private JPanel linkPanel;
	private Click click;

	public RPNavbar getNavbar() {
		return navbar;
	}

	public void setNavbar(RPNavbar navbar) {
		this.navbar = navbar;
	}

	public int getNavigationAlignment() {
		return navigationAlignment;
	}

	public void setNavigationAlignment(int alignment) {
		this.navigationAlignment = alignment;
	}

	public boolean isLeftNode() {
		return isLeftNode;
	}

	public void setLeftNode(boolean isLeftNode) {
		this.isLeftNode = isLeftNode;
	}

	public JPanel getContainerPanel() {
		return containerPanel;
	}

	public void setContainerPanel(JPanel containerPanel) {
		this.containerPanel = containerPanel;
	}

	public JPanel getLinkPanel() {
		return linkPanel;
	}

	public void setLinkPanel(JPanel linkPanel) {
		this.linkPanel = linkPanel;
	}

	public RPNavlink(String title) {
		this(title, null, null, null);
	}

	public RPNavlink(Icon icon) {
		this(null, icon, null, null);
	}

	public RPNavlink(String title, JPanel containerPanel, JPanel linkPanel) {
		this(title, null, containerPanel, linkPanel);
	}

	public RPNavlink(Icon icon, JPanel containerPanel, JPanel linkPanel) {
		this(null, icon, containerPanel, linkPanel);
	}

	public RPNavlink(String title, Icon icon, JPanel containerPanel,
			JPanel linkPanel) {
		super(title, icon);
		this.containerPanel = containerPanel;
		this.linkPanel = linkPanel;

		setOpaque(false);

		setBackground(Color.decode("#F6F6F6"));

		setFont(getFont().deriveFont(16f));

		setBorder(BorderFactory.createEmptyBorder());

		setMargin(new Insets(0, 8, 0, 8));

		NavlinkUI navUI = new NavlinkUI();
		setUI(navUI);

		addMouseListener(new MouseListener() {
			private Color colorSave;
			private int cursorSave;

			public void mouseEntered(MouseEvent e) {
				if (click != null) {
					click.move();
				}
				colorSave = RPNavlink.this.getForeground();
				cursorSave = RPNavlink.this.getCursor().getType();
				RPNavlink.this.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
				RPNavlink.this.setForeground(RPNavlink.this.getForeground()
						.darker());
			}

			public void mouseExited(MouseEvent e) {
				if (click != null) {
					click.exit();
				}
				RPNavlink.this.setForeground(colorSave);
				RPNavlink.this
						.setCursor(Cursor.getPredefinedCursor(cursorSave));
			}

			public void mouseReleased(MouseEvent e) {
				if (click != null) {
					click.up();
				}
			}

			public void mousePressed(MouseEvent e) {
				route();
				if (click != null) {
					click.down();
				}
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
	}
	
	public void setClick(Click click){
		this.click = click;
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);

		if (this.titleLabel != null) {
			this.titleLabel.setForeground(fg);
		}
	}

	public void route() {
		if (containerPanel != null && linkPanel != null) {
			containerPanel.removeAll();
			containerPanel.add(linkPanel, "grow");
			containerPanel.revalidate();
			containerPanel.repaint();
		}
	}
}
