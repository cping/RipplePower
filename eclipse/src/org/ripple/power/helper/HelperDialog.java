package org.ripple.power.helper;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.RPPushTool;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.SwingUtils;

public class HelperDialog extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Image offscreenImg;

	Image[] faceImage;
	private Message NowSerif;
	int fx = 126;
	int fy = 2;
	int fwidth = 756;
	int fheight = 150;
	BufferedImage _backimage;
	BufferedImage _faceimage;

	private static RPPushTool instance = null;

	private static RPPushTool load() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				LSystem.applicationMain.getGraphicsConfiguration());
		HelperDialog helper = new HelperDialog();
		helper.setSize(new Dimension(GraphicTool.Width_MaidSystem,
				GraphicTool.Height_MaidSystem));
		helper.setPreferredSize(new Dimension(GraphicTool.Width_MaidSystem,
				GraphicTool.Height_MaidSystem));
		helper.setBackground(LColor.black);
		return RPPushTool.pop(
				new Point((size.width - GraphicTool.Width_MaidSystem) / 2, size
						.getHeight()),
				(int) (screenInsets.bottom + helper.getHeight() + 90),
				LangConfig.get(HelperDialog.class, "ripple_wizard",
						"Ripple Wizard"), helper);
	}

	public static void hideDialog() {
		if (instance != null) {
			instance.setVisible(false);
		}
	}
	
	public static void showDialog() {
		if (instance != null) {
			instance.setVisible(true);
		}
	}
	
	public synchronized static RPPushTool get() {
		if (instance == null) {
			instance = load();
		} else if (instance.isClose()) {
			instance.close();
			instance = load();
		}
		return instance;
	}

	public static void hideSystem() {
		if (instance != null) {
			if (instance.isVisible() && instance.getOpacity() == 1f
					&& !instance.isClose()) {
				SwingUtils.fadeOut(instance.getDialog(), false);
			}
		}
	}

	public static void showSystem() {
		if (instance != null) {
			if (!instance.isVisible() && instance.getOpacity() == 0f
					&& !instance.isClose()) {
				SwingUtils.fadeIn(instance.getDialog());
			}
		}
	}

	HelperDialog() {
		faceImage = GraphicsUtils.getSplitImages("icons/face.png", 96, 96);
		GraphicTool tools = new GraphicTool();
		_backimage = tools.getWinTable(fwidth, fheight, Color.white,
				LSystem.background, true);
		_faceimage = tools.getTable(faceImage[0].getWidth(null),
				faceImage[0].getHeight(null));
		NowSerif = new Message(0, 0, "Hello,Ripple World!");
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (offscreenImg == null) {
			offscreenImg = createImage(getWidth(), getHeight());
			GraphicTool.get().loadWait(offscreenImg);
		}
		if (offscreenImg != null) {
			Graphics offscreenG = offscreenImg.getGraphics();
			offscreenG.setColor(getBackground());
			offscreenG.clearRect(0, 0, getWidth(), getHeight());
			draw(offscreenG);
			g.drawImage(offscreenImg, 0, 0, null);
		}
	}

	public void drawFace(Graphics g, int x, int y) {
		if (faceImage[0] != null) {
			g.drawImage(faceImage[0], x, y, this);
			g.drawImage(_faceimage, x, y, this);
		}
	}

	private Font deffont = new Font("Dialog", 1, 20);

	private void draw(Graphics g) {
		if (NowSerif == null) {
			return;
		}
		drawFace(g, 18, fy + 24);
		g.drawImage(_backimage, fx, fy, this);
		g.setColor(Color.white);
		g.setFont(deffont);
		GraphicsUtils.setAntialias(g, true);
		String MessageArray[] = changeArray(NowSerif.Message, 23, fwidth);
		for (int i = 0; i < MessageArray.length; i++) {
			g.drawString(MessageArray[i],
					(int) Math.round(fx + 0.029D * fwidth),
					(int) Math.round(fy + 0.29D * (i + 1) * fheight));
		}
	}

	private String[] changeArray(String str, int f, int w) {
		int line = (int) Math.round((1.0D * w) / f);
		int size = str.length();
		if (size == 0)
			return new String[0];
		int R = (size - 1) / line + 1;
		String strs[] = new String[R];
		for (int i = 0; i < R; i++) {
			int last;
			if (i == R - 1)
				last = size;
			else
				last = (i + 1) * line;
			strs[i] = str.substring(i * line, last);
		}

		return strs;
	}

}
