package org.ripple.power.ui.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class RPSplash extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FontMetrics fontMetrics;

	private Window window;

	private Image image;

	private Image _offscreenImg;

	private Graphics _offscreen;

	private int progress;

	private String version;

	private String build;

	private final Color progressColour;

	private final Color gradientColour;

	private final Color buildTextColour;

	private int versionLabelX;

	private int versionLabelY;

	private int buildLabelX;

	private int buildLabelY;

	private static final int PROGRESS_HEIGHT = 15;

	private final Color versionTextColour;

	public RPSplash(Color progressBarColour, String imageResourcePath,
			String build, Color buildTextColour, int buildLabelX,
			int buildLabelY, String versionNumber, boolean autonClose,
			Updateable update) {
		this(progressBarColour, imageResourcePath, build, buildTextColour,
				buildLabelX, buildLabelY, versionNumber, -1, -1, autonClose,
				update);
	}

	public RPSplash(Color progressBarColour, String imageResourcePath,
			String build, Color buildTextColour, int buildLabelX,
			int buildLabelY, String versionNumber, int versionLabelX,
			int versionLabelY, boolean autonClose, Updateable update) {
		this(progressBarColour, imageResourcePath, build, buildTextColour,
				buildLabelX, buildLabelY, versionNumber, UIConfig
						.getBrandColor(), versionLabelX, versionLabelY,
				autonClose, update);
	}

	public RPSplash(Color progressBarColour, String imageResourcePath,
			String buildString, Color buildTextColour, int buildLabelX,
			int buildLabelY, String versionNumber, Color versionTextColour,
			int versionLabelX, int versionLabelY, boolean autonClose,
			final Updateable update) {

		this.buildTextColour = buildTextColour;
		this.buildLabelX = buildLabelX;
		this.buildLabelY = buildLabelY;
		this.versionTextColour = versionTextColour;
		this.versionLabelX = versionLabelX;
		this.versionLabelY = versionLabelY;

		progressColour = progressBarColour;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(Color.white);

		gradientColour = UIRes.getBrighter(progressBarColour, 0.75);

		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		setFont(font);
		fontMetrics = getFontMetrics(font);

		image = LImage.createImage(imageResourcePath).getBufferedImage();

		if (buildString != null) {
			build = buildString;
		}
		if (versionNumber != null) {
			version = versionNumber;
		}

		Dimension size = new Dimension(image.getWidth(this),
				image.getHeight(this));
		window = new Window(new Frame());
		window.setSize(size);
		window.setLayout(new BorderLayout());
		window.add(BorderLayout.CENTER, this);
		window.setLocation(UIRes.getPointToCenter(window, size));
		window.validate();
		window.setVisible(true);

		if (autonClose) {
			Updateable call = new Updateable() {
				@Override
				public void action(Object o) {
					for (; progress < 10;) {
						process();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					RPSplash.this.dispose();
					if (update != null) {
						update.action(progress);
					}
				}
			};
			LSystem.postThread(call);
		}
	}

	public int getProgress() {
		return progress;
	}

	public synchronized void process() {
		progress++;
		repaint();
	}

	public synchronized void paint(Graphics g) {
		Dimension size = getSize();
		if (_offscreenImg == null) {
			_offscreenImg = createImage(size.width, size.height);
			_offscreen = _offscreenImg.getGraphics();
		}

		_offscreen.drawImage(image, 0, 0, this);
		_offscreen.setColor(progressColour);
		Graphics2D offscreen2d = (Graphics2D) _offscreen;

		offscreen2d.setPaint(new GradientPaint(0, image.getHeight(this)
				- PROGRESS_HEIGHT, gradientColour, 0, image.getHeight(this),
				progressColour));
		_offscreen.fillRect(0, image.getHeight(this) - PROGRESS_HEIGHT,
				(window.getWidth() * progress) / 9, PROGRESS_HEIGHT);

		if (version != null) {
			if (versionLabelX == -1) {
				versionLabelX = (getWidth() - fontMetrics.stringWidth(version)) / 2;
			}

			if (versionLabelY == -1) {
				versionLabelY = image.getHeight(this) - PROGRESS_HEIGHT
						- fontMetrics.getHeight();
			}

			GraphicsUtils.setAntialiasAll(offscreen2d, true);

			_offscreen.setColor(buildTextColour);
			_offscreen.setFont(GraphicsUtils.getFont(Font.SANS_SERIF, 1, 28));
			_offscreen.drawString(build, buildLabelX, buildLabelY);

			_offscreen.setColor(versionTextColour);
			_offscreen.setFont(GraphicsUtils.getFont(14));
			_offscreen.drawString(version, versionLabelX, versionLabelY);
		}

		g.drawImage(_offscreenImg, 0, 0, this);
	}

	public void dispose() {
		SwingUtils.close(window);
	}

	public void update(Graphics g) {
		paint(g);
	}

}
