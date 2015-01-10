package org.ripple.power.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.Cycle;
import org.ripple.power.ui.graphics.LFont;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.utils.MathUtils;

public class WaitDialog {
	private static WaitDialog lock = null;
	private boolean isRunning = false;

	private RPDialogTool tool;

	public WaitDialog(Window parent) {
		Dimension dim = new Dimension(400, 128);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(UIConfig.dialogbackground);
		panel.setPreferredSize(dim);
		panel.setSize(dim);
		isRunning = true;
		new ShowPanel(panel, dim.width, dim.height);
		tool = RPDialogTool.show(parent, "Transaction Broadcast", panel, -1,
				-1, true, LSystem.MINUTE);
	}

	public static WaitDialog showDialog(Window parent, boolean show) {
		if (show) {
			synchronized (WaitDialog.class) {
				if (lock == null) {
					return (lock = new WaitDialog(parent));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new WaitDialog(parent);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static WaitDialog showDialog(Window parent) {
		return showDialog(parent, true);
	}

	public RPDialogTool get() {
		return tool;
	}

	public void closeDialog() {
		synchronized (WaitDialog.class) {
			isRunning = false;
			tool.close();
			lock = null;
		}
	}

	class ShowPanel extends Canvas {

		private static final long serialVersionUID = 1L;
		private final BufferedImage image;

		ShowPanel(final JPanel panel, final int w, final int h) {
			final String message = "Broadcasting transaction .... please wait";
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			setBackground(UIConfig.dialogbackground);
			Updateable update = new Updateable() {
				public void action(Object o) {
					int width = w / 2;
					int height = h / 2;
					int index = MathUtils.random(0, 6);
					if (index == 4) {
						index = 2;
					}
					Cycle cycle = Cycle.newSample(index, width, height);
					LGraphics g = new LGraphics(image);
					LFont font = LFont.getFont(18);
					for (; isRunning;) {
						synchronized (image) {
							g.setColor(UIConfig.dialogbackground);
							g.fillRect(0, 0, getWidth(), getHeight());
							if (cycle != null) {
								switch (index) {
								case 0:
									g.translate(105, 22);
									break;
								case 1:
									g.translate(155, 12);
									g.scale(0.5, 0.5);
									break;
								case 2:
									g.translate(144, 7);
									g.scale(0.5, 0.5);
									break;
								case 3:
									g.translate(100, 40);
									break;
								case 5:
									g.translate(160, 15);
									g.scale(0.5, 0.5);
									break;
								case 6:
									g.translate(160, 15);
									g.scale(0.5, 0.5);
									break;
								default:
									g.translate(144, 7);
									g.scale(0.5, 0.5);
									break;
								}
								cycle.update(30);
								cycle.createUI(g);
								g.restore();
							}
							g.setAntiAlias(true);
							g.setFont(font);
							g.drawString(message,
									(w - font.stringWidth(message)) / 2 - 5,
									(h - font.getHeight()) / 2 + 15);
							g.setAntiAlias(false);
						}
						if (panel != null && isRunning
								&& panel.getGraphics() != null) {
							panel.update(panel.getGraphics());
							panel.repaint();
						}
						repaint();
						try {
							Thread.sleep(45);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			LSystem.postThread(update);
			panel.add(this);
		}

		public void update(Graphics g) {
			paint(g);
		}

		@Override
		public synchronized void paint(Graphics g) {
			if (image != null) {
				synchronized (image) {
					g.drawImage(image, 0, 0, this);
				}
			}
		}

	}

}
