package org.ripple.power.ui;

import java.awt.Canvas;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.Cycle;
import org.ripple.power.ui.graphics.LFont;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.SwingUtils;

public class WaitDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isRunning = false;

	public WaitDialog(Window parent) {
		super(parent, "Transaction Broadcast",
				Dialog.ModalityType.MODELESS);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension dim = new Dimension(400, 150);
		setResizable(false);
		setPreferredSize(dim);
		setSize(dim);
		isRunning = true;
		new ShowPanel(this, dim.width, dim.height);
	}

	public static WaitDialog showDialog(Window parent) {
		final WaitDialog dialog = new WaitDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		Updateable update = new Updateable() {
			public void action(Object o) {
				for (int i = 0; dialog != null && i < 20 && dialog.isVisible(); i++) {
					try {
						Thread.sleep(LSystem.SECOND);
					} catch (InterruptedException e) {
					}
				}
				dialog.closeDialog();
			}
		};
		LSystem.postThread(update);
		return dialog;
	}

	public void closeDialog() {
		isRunning = false;
		SwingUtils.close(this);
	}

	class ShowPanel extends Canvas {

		private static final long serialVersionUID = 1L;
		private final BufferedImage image;

		ShowPanel(final Window window, final int w, final int h) {
			final String message = "Broadcasting transaction .... please wait";
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			setBackground(LSystem.dialogbackground);
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
							g.setColor(LSystem.dialogbackground);
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
							g.setAntialiasAll(true);
							g.setFont(font);
							g.drawString(message,
									(w - font.stringWidth(message)) / 2 - 5,
									(h - font.getHeight()) / 2);
							g.setAntialiasAll(false);
						}
						window.update(window.getGraphics());
						repaint();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			LSystem.postThread(update);
			window.add(this);
		}

		public void update(Graphics g) {
			paint(g);
		}

		@Override
		public void paint(Graphics g) {
			if (image != null) {
				synchronized (image) {
					g.drawImage(image, 0, 0, this);
				}
			}
		}

	}

}
