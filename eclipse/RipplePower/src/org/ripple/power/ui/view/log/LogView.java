package org.ripple.power.ui.view.log;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LColor;

public class LogView {

	private static final int MAX_LINE = 2048;
	private static LogView instance;
	private JFrame frame;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JViewport viewpoint;
	private int lastScrollHeight = -1;
	private int lastViewHeight = -1;
	private boolean changedFlg = false;
	private boolean close = false;
	private StringBuffer buffer = new StringBuffer();
	private LogAppender logAppender;

	public static LogView get() {
		if (instance == null) {
			instance = new LogView();
		}
		return instance;
	}

	public LogView() {
		this(LSystem.applicationName + " Log View", 640, 480);
	}

	public LogView(String title) {
		this(title, 640, 480);
	}

	public LogView(String title, int width, int height) {
		frame = new JFrame(title);
		if (width != -1 && height != -1) {
			Dimension dim = new Dimension(width, height);
			frame.setPreferredSize(dim);
			frame.setSize(dim);
		}
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				closed();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				closed();
			}

			@Override
			public void windowActivated(WindowEvent e) {

			}
		});
		frame.setIconImage(UIRes.getIcon());
		frame.pack();
		frame.setLocationRelativeTo(LSystem.applicationMain);

		Font font = UIRes.getFont();

		textArea = new JTextArea();
		textArea.setFont(font);
		textArea.setEditable(false);
		textArea.setBackground(UIConfig.dialogbackground);
		textArea.setForeground(LColor.white);

		scrollPane = new JScrollPane(textArea);
		viewpoint = scrollPane.getViewport();
		{
			viewpoint.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					Rectangle rect = viewpoint.getViewRect();

					if (lastScrollHeight == textArea.getHeight() && lastViewHeight == rect.height
							&& changedFlg == false) {
						return;
					}

					if (lastScrollHeight - lastViewHeight == rect.y) {
						rect.setLocation(rect.x, textArea.getHeight() - rect.height);
						textArea.scrollRectToVisible(rect);
					}

					lastScrollHeight = textArea.getHeight();
					lastViewHeight = rect.height;
					changedFlg = false;
				}
			});
		}
		frame.add(scrollPane);
		(logAppender = new LogAppender()).start();
	}

	public void show() {
		if (!frame.isVisible()) {
			frame.setVisible(true);
			frame.setState(JFrame.NORMAL);
		}
		this.close = false;
		if (logAppender == null) {
			(logAppender = new LogAppender()).start();
		}
	}

	public void hide() {
		if (frame.isVisible()) {
			frame.setVisible(false);
		}
	}

	public void closed() {
		this.close = true;
		if (logAppender != null) {
			logAppender.interrupt();
			logAppender = null;
		}
		this.hide();
	}

	public Rectangle getBounds() {
		return frame.getBounds();
	}

	public void setBounds(Rectangle r) {
		frame.setBounds(r);
	}

	public void append(String str) {
		synchronized (buffer) {
			buffer.append(str);
			buffer.notify();
		}
	}

	public void append(byte[] b) {
		synchronized (buffer) {
			buffer.append(b);
			buffer.notify();
		}
	}

	public void append(byte[] b, int off, int len) {
		append(new String(b, off, len));
	}

	public void append(int b) {
		synchronized (buffer) {
			buffer.append(b);
			buffer.notify();
		}
	}

	private class LogAppender extends Thread {

		public void run() {
			for (; !close;) {
				synchronized (buffer) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
					}
					textArea.append(buffer.toString());
					buffer.delete(0, buffer.length());
				}
				if (textArea.getLineCount() > MAX_LINE) {
					try {
						int offset = textArea.getLineEndOffset(textArea.getLineCount() - MAX_LINE - 1);
						textArea.getDocument().remove(0, offset);
					} catch (Exception exc) {
						ErrorLog.get().logException("LogView Appender", exc);
					}
				}
				changedFlg = true;
			}
		}
	}

}