package org.ripple.power.helper;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ripple.power.utils.GraphicsUtils;

public class MaidSystem extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Image offscreenImg;
	private GraphicTool Tools;

	Image[] faceImage;
	private Message NowSerif;

	public MaidSystem() {
		super(Paramaters.getContainer(), "Ripple助手", false);
		faceImage = GraphicsUtils.getSplitImages("icons/face.png", 96, 96);
		NowSerif = new Message(0, 0, "Hello,Ripple World!");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		setLocation((screenSize.width-Paramaters.Width_MaidSystem)/2, (int)screenSize.getHeight() - Paramaters.Height_MaidSystem - 100);
		setPreferredSize(new Dimension(Paramaters.Width_MaidSystem, Paramaters.Height_MaidSystem));
		setResizable(false);
		Tools = new GraphicTool();
		setBackground(Color.black);
		pack();
		setVisible(true);
	
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (offscreenImg == null) {
			offscreenImg = createImage(900, 200);
			Paramaters.Image_BOX.loadWait(offscreenImg, this);
		}
		Graphics offscreenG = offscreenImg.getGraphics();
		offscreenG.setColor(getBackground());
		offscreenG.fillRect(0, 0, 900, 200);
		offscreenG.setColor(Color.white);
		draw(offscreenG);
		g.drawImage(offscreenImg, 0, 0, this);
	}

	public void drawFace(Graphics g, int x, int y) {
		if (faceImage[0] != null) {
			g.drawImage(faceImage[0], x, y, null);
			if (Tools != null) {
				Tools.drawFrame(g, null, x, y, faceImage[0].getWidth(null),
						faceImage[0].getHeight(null));
			}
		}
	}

	private Font deffont = new Font("Dialog", 1, 20);

	private void draw(Graphics g) {
		if (NowSerif == null) {
			return;
		}

		int fx = (int) Math.round(126.00000000000001D);
		int fy = (int) Math.round(25D);
		int fwidth = (int) Math.round(756D);
		int fheight = (int) Math.round(150D);

		drawFace(g, (int) Math.round(18D), (int) Math.round(50D));
		if(Tools!=null){
		Tools.drawFrame(g, this, fx, fy, fwidth, fheight);
		}
		g.setColor(Color.white);
		g.setFont(deffont);
		String MessageArray[] = changeArray(NowSerif.Message, 23, fwidth);
		for (int i = 0; i < MessageArray.length; i++) {
			g.drawString(
					MessageArray[i],
					(int) Math.round((double) fx + 0.029999999999999999D
							* (double) fwidth),
					(int) Math.round((double) fy + 0.29999999999999999D
							* (double) (i + 1) * (double) fheight));
		}
	}

	private String[] changeArray(String str, int f, int w) {
		int line = (int) Math.round((1.0D * (double) w) / (double) f);
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
