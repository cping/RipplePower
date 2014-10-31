package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.utils.SwingUtils;

public class RPDialogTool {

	public final static int TITLE_SIZE = 20;

	public static class BaseDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final int width, height;

		private final int x, y;

		boolean closed;

		final Window parent;

		public BaseDialog(final Window parent, final int x, int y,
				final int width, final int height) {
			super(parent, Dialog.ModalityType.MODELESS);
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.parent = parent;
			initComponents();
		}

		private void initComponents() {
			this.setSize(width, height);
			if (x == -1 && y == -1) {
				this.setLocationRelativeTo(parent);
			} else {
				this.setLocation(x, y);
			}
			this.setBackground(LColor.black);
		}

		public void close() {
			closed = true;
			SwingUtils.fadeOut(this, true);
		}
	}

	private JPanel _headPane = null;
	private JPanel _backPane = null;
	private JPanel _btnPane = null;
	private RPLabel _titleLabel = null;
	private RPLabel _closeLabel = null;

	private JPanel _backagePane = null;

	private Point _oldPos;
	private BaseDialog _baseDialog = null;

	private int _width = -1, _height = -1;

	public RPDialogTool(final Window parent, final String title,
			final Component comp, final int x, final int y, final int w,
			final int h, final boolean autoClose, final long closeTime) {
		_width = w;
		_height = h;
		_baseDialog = new BaseDialog(parent, x, y, _width, _height);
		_headPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_backPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_btnPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_titleLabel = new RPLabel(title);
		_closeLabel = new RPLabel("X");
		_closeLabel.setHorizontalAlignment(JLabel.CENTER);

		_backagePane = new JPanel(new BorderLayout());

		((JPanel) _baseDialog.getContentPane())
				.setBackground(LSystem.dialogbackground);
		_headPane.setBackground(LSystem.dialogbackground);
		_backPane.setBackground(LSystem.dialogbackground);
		_btnPane.setBackground(LSystem.dialogbackground);

		Dimension dim = new Dimension(_width, TITLE_SIZE);

		_headPane.setPreferredSize(dim);
		_headPane.setSize(dim);

		_baseDialog.getRootPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
		_titleLabel.setFont(new Font(LangConfig.fontName, 0, 12));
		_titleLabel
				.setPreferredSize(new Dimension(_width - 40, TITLE_SIZE - 4));
		_titleLabel.setVerticalTextPosition(RPLabel.CENTER);
		_titleLabel.setHorizontalTextPosition(RPLabel.LEFT);

		_closeLabel.setFont(new Font("Arial", Font.BOLD, 12));

		dim = new Dimension(25, 20);

		_closeLabel.setPreferredSize(dim);
		_closeLabel.setSize(dim);

		_closeLabel.setVerticalTextPosition(RPLabel.CENTER);
		_closeLabel.setHorizontalTextPosition(RPLabel.RIGHT);
		_closeLabel.setCursor(new Cursor(12));
		_closeLabel.setToolTipText(LangConfig.get(this, "close", "Close"));

		_backagePane.setPreferredSize(new Dimension(_width, _height));
		_backagePane.setBorder(LineBorder.createBlackLineBorder());

		_backagePane.add(comp);

		_headPane.add(_titleLabel);
		_headPane.add(_closeLabel);
		_backPane.add(_backagePane);

		_baseDialog.add(_headPane, BorderLayout.NORTH);
		_baseDialog.add(_backPane, BorderLayout.CENTER);

		_titleLabel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				Point newP = new Point(e.getXOnScreen(), e.getYOnScreen());
				double x = _baseDialog.getX() + (newP.x - _oldPos.x);
				double y = _baseDialog.getY() + (newP.y - _oldPos.y);
				_baseDialog.setLocation((int) x, (int) y);
				_oldPos = newP;
			}
		});
		_titleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				_oldPos = new Point(e.getXOnScreen(), e.getYOnScreen());
			}
		});
		_closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				_baseDialog.close();
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				_closeLabel.setBorder(BorderFactory
						.createLineBorder(Color.gray));
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				_closeLabel.setBorder(null);
			}
		});

		// _baseDialog.setAlwaysOnTop(true);
		_baseDialog.setUndecorated(true);
		_baseDialog.setResizable(false);
		_baseDialog.setVisible(true);

		if (autoClose) {
			Updateable update = new Updateable() {
				@Override
				public void action(Object o) {
					try {
						Thread.sleep(closeTime < 1 ? 1 : closeTime);
					} catch (InterruptedException e) {
					}
					_baseDialog.close();
				}
			};
			LSystem.postThread(update);
		}
	}

	public boolean isClose() {
		return _baseDialog.closed;
	}

	public void setVisible(boolean v) {
		_baseDialog.setVisible(v);
	}

	public boolean isVisible() {
		return _baseDialog.isVisible();
	}

	public void setOpacity(float opacity) {
		_baseDialog.setOpacity(opacity);
	}

	public float getOpacity() {
		return _baseDialog.getOpacity();
	}

	public void close() {
		_baseDialog.close();
	}

	public Dialog getDialog() {
		return _baseDialog;
	}

	public static RPDialogTool show(Window parent, String title,
			Component comp, int x, int y, int w, int h) {
		return show(parent, title, comp, x, y, w, h, false, 1);
	}

	public static RPDialogTool show(Window parent, String title,
			Component comp, int x, int y, boolean ac, long time) {
		return show(parent, title, comp, x, y, comp.getWidth(),
				comp.getHeight(), ac, time);
	}

	public static RPDialogTool show(Window parent, String title,
			Component comp, boolean ac, long time) {
		return show(parent, title, comp, -1, -1, comp.getWidth(),
				comp.getHeight(), ac, time);
	}

	public static RPDialogTool show(Window parent, String title,
			Component comp, int x, int y, int w, int h, boolean ac, long time) {
		return new RPDialogTool(parent, title, comp, x, y, w, h
				+ RPDialogTool.TITLE_SIZE, ac, time);
	}
}
