package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;

public class RPBubbleDialog {

	public static class TipDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static Dimension dim;
		private int x, y;
		private final int width, height;
		private static Insets screenInsets;

		public TipDialog(final int width, final int height) {
			this.width = width;
			this.height = height;
			dim = Toolkit.getDefaultToolkit().getScreenSize();
			screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
					this.getGraphicsConfiguration());
			x = (int) (dim.getWidth() - width - 3);
			y = (int) (dim.getHeight() - screenInsets.bottom - 3);
			initComponents();
		}

		public void run() {
			for (int i = 0; i <= height; i += 10) {
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
			dispose();
		}
	}

	private JPanel _headPane = null;
	private JPanel _backPane = null;
	private JPanel _btnPane = null;
	private RPLabel _titleLabel = null;
	private RPLabel _closeLabel = null;
	private RPTextArea _contentText = null;
	private JScrollPane _backagePane = null;
	private RPLabel _updateLabel = null;

	private Point _oldPos;
	private TipDialog _tpDialog = null;

	private int _width = 300, _height = 120;
	private String _message;

	public RPBubbleDialog(final String mes, final boolean autoClose) {
		this(mes, 300, 120, autoClose);
	}

	public RPBubbleDialog(final String mes, final int w, final int h,
			final boolean autoClose) {
		_message = mes;
		_width = w;
		_height = h;

		_tpDialog = new TipDialog(_width, _height);

		_headPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_backPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_btnPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		_titleLabel = new RPLabel(LangConfig.get(this, "news", "News"));
		_closeLabel = new RPLabel("X");
		_closeLabel.setHorizontalAlignment(JLabel.CENTER);
		_contentText = new RPTextArea(_message);
		_backagePane = new JScrollPane(_contentText);
		_updateLabel = new RPLabel();

		((JPanel) _tpDialog.getContentPane())
				.setBackground(LSystem.dialogbackground);
		_headPane.setBackground(LSystem.dialogbackground);
		_backPane.setBackground(LSystem.dialogbackground);
		_btnPane.setBackground(LSystem.dialogbackground);

		_headPane.setPreferredSize(new Dimension(300, 30));

		_tpDialog.getRootPane().setBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
		_titleLabel.setFont(new Font(LangConfig.fontName, 0, 14));
		_titleLabel.setPreferredSize(new Dimension(260, 26));
		_titleLabel.setVerticalTextPosition(RPLabel.CENTER);
		_titleLabel.setHorizontalTextPosition(RPLabel.CENTER);

		_closeLabel.setFont(new Font("Arial", Font.BOLD, 15));
		_closeLabel.setPreferredSize(new Dimension(20, 20));
		_closeLabel.setVerticalTextPosition(RPLabel.CENTER);
		_closeLabel.setHorizontalTextPosition(RPLabel.CENTER);
		_closeLabel.setCursor(new Cursor(12));
		_closeLabel.setToolTipText(LangConfig.get(this, "close", "Close"));

		_contentText.setEditable(false);
		_contentText.setFont(new Font(LangConfig.fontName, Font.PLAIN, 13));
		_contentText.setForeground(LColor.white);
		_contentText.setLineWrap(true);
		_contentText.setText(_message);
		_backagePane.setPreferredSize(new Dimension(_width - 10, 100));
		_backagePane.setBorder(LineBorder.createBlackLineBorder());

		_updateLabel.setPreferredSize(new Dimension(110, 30));
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
				int x = _tpDialog.getX() + (newP.x - _oldPos.x);
				int y = _tpDialog.getY() + (newP.y - _oldPos.y);
				_tpDialog.setLocation(x, y);
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

	public static void pop(final String msg) {
		new RPBubbleDialog(msg, false);
	}

	public static void pop(final String msg, final boolean ac) {
		new RPBubbleDialog(msg, ac);
	}
}
