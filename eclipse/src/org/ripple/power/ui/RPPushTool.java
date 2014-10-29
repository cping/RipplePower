package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import java.awt.Toolkit;
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

public class RPPushTool {

	public final static int TITLE_SIZE = 30;

	public static class TipDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static Dimension dim;
		private int x, y, endTop;
		private final int width, height;
		private static Insets screenInsets;

		boolean closed;

		public TipDialog(final Point start, final int end, final int width,
				final int height) {
			super(LSystem.applicationMain,  Dialog.ModalityType.MODELESS);
			this.width = width;
			this.height = height;
			dim = Toolkit.getDefaultToolkit().getScreenSize();
			screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
					this.getGraphicsConfiguration());
			if (end < 0) {
				this.endTop = height;
			} else {
				this.endTop = end;
			}
			if (start == null) {
				x = (int) (dim.getWidth() - width - 3);
				y = (int) (dim.getHeight() - screenInsets.bottom - 3);
			} else {
				x = (int) start.x;
				y = (int) start.y;
			}
			initComponents();
		}

		public void run() {
			for (int i = 0; i <= endTop; i += 10) {
				try {
					this.setLocation(x, y - i);
					Thread.sleep(5);
				} catch (InterruptedException ex) {
				}
			}
		}

		private void initComponents() {
			this.setSize(width, height);
			this.setLocation(x, y);
			this.setBackground(LColor.black);
		}

		public void close() {
			x = this.getX();
			y = this.getY();
			int ybottom = (int) dim.getHeight() - screenInsets.bottom;
			for (int i = 0; i <= ybottom - y; i += 10) {
				try {
					setLocation(x, y + i);
					Thread.sleep(5);
				} catch (InterruptedException ex) {
				}
			}
			closed = true;
			SwingUtils.close(this);
		}
	}

	private JPanel _headPane = null;
	private JPanel _backPane = null;
	private JPanel _btnPane = null;
	private RPLabel _titleLabel = null;
	private RPLabel _closeLabel = null;

	private JPanel _backagePane = null;
	private RPLabel _updateLabel = null;

	private Point _oldPos;
	private TipDialog _tpDialog = null;

	private int _width = -1, _height = -1;

	public RPPushTool(final Point start, final int endTop, final String title,
			final Component comp, final int w, final int h,
			final boolean autoClose) {

		_width = w;
		_height = h;

		_tpDialog = new TipDialog(start, endTop, _width, _height);

		_headPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_backPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_btnPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_titleLabel = new RPLabel(title);
		_closeLabel = new RPLabel("X");
		_closeLabel.setHorizontalAlignment(JLabel.CENTER);

		_backagePane = new JPanel(new BorderLayout());
		_updateLabel = new RPLabel();

		((JPanel) _tpDialog.getContentPane())
				.setBackground(LSystem.dialogbackground);
		_headPane.setBackground(LSystem.dialogbackground);
		_backPane.setBackground(LSystem.dialogbackground);
		_btnPane.setBackground(LSystem.dialogbackground);

		Dimension dim = new Dimension(_width, TITLE_SIZE);

		_headPane.setPreferredSize(dim);
		_headPane.setSize(dim);

		_tpDialog.getRootPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
		_titleLabel.setFont(new Font(LangConfig.fontName, 0, 14));
		_titleLabel
				.setPreferredSize(new Dimension(_width - 40, TITLE_SIZE - 4));
		_titleLabel.setVerticalTextPosition(RPLabel.CENTER);
		_titleLabel.setHorizontalTextPosition(RPLabel.LEFT);

		_closeLabel.setFont(new Font("Arial", Font.BOLD, 15));

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

		_updateLabel.setPreferredSize(new Dimension(_width, TITLE_SIZE));
		_updateLabel.setCursor(new Cursor(12));

		_headPane.add(_titleLabel);
		_headPane.add(_closeLabel);
		_backPane.add(_backagePane);

		_btnPane.add(_updateLabel);
		_tpDialog.add(_headPane, BorderLayout.NORTH);
		_tpDialog.add(_backPane, BorderLayout.CENTER);
		_tpDialog.add(_btnPane, BorderLayout.SOUTH);

		_updateLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				_tpDialog.close();
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				_updateLabel.setBorder(BorderFactory
						.createLineBorder(Color.gray));
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				_updateLabel.setBorder(null);
			}
		});

		_titleLabel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				Point newP = new Point(e.getXOnScreen(), e.getYOnScreen());
				double x = _tpDialog.getX() + (newP.x - _oldPos.x);
				double y = _tpDialog.getY() + (newP.y - _oldPos.y);
				_tpDialog.setLocation((int) x, (int) y);
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
				_tpDialog.close();
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

		_tpDialog.setAlwaysOnTop(true);
		_tpDialog.setUndecorated(true);
		_tpDialog.setResizable(false);
		_tpDialog.setVisible(true);
		_tpDialog.run();

		if (autoClose) {
			Updateable update = new Updateable() {
				@Override
				public void action(Object o) {
					try {
						Thread.sleep(LSystem.SECOND * 20);
					} catch (InterruptedException e) {
					}
					_tpDialog.close();
				}
			};
			LSystem.postThread(update);
		}
	}

	public boolean isClose() {
		return _tpDialog.closed;
	}

	public Dialog getDialog() {
		return _tpDialog;
	}

	public static RPPushTool pop(Point start, int end, String title,
			Component comp, int w, int h) {
		return pop(start, end, title, comp, w, h, false);
	}

	public static RPPushTool pop(Point start, int end, String title,
			Component comp, int w, int h, boolean ac) {
		return new RPPushTool(start, end, title, comp, w, h, ac);
	}
}
