package org.ripple.power.helper;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.ripple.power.utils.GraphicsUtils;

public class GraphicTool {

    public static final int Width_MaidSystem = 886;
    public static final int Height_MaidSystem = 158;
    public static final int defaultFrameSize = 0;
    public static int frameTop = 0;
    public static int frameLeft = 0;
    private static ImageSet Image_BOX;

    public static ImageSet get()
    {
    	if(Image_BOX==null){
        Image_BOX = new ImageSet();
        Image image =  GraphicsUtils.loadImage("icons/win.png");
        image = GraphicsUtils.transparencyBlackColor(image);
        Image_BOX.SplitWindow(GraphicsUtils.getBufferImage(image));
    	}
    	return Image_BOX;
    }
	public GraphicTool() {
		MenuItemColor = Color.yellow;
	}

	public void setTransmission(Graphics g, int x, int y, int wide, int high,
			Color col, float t) {
		Graphics2D g2 = (Graphics2D) g;
		java.awt.Composite comp_defo = g2.getComposite();
		AlphaComposite composite = AlphaComposite.getInstance(3, t);
		g2.setComposite(composite);
		g2.setColor(col);
		g2.fillRect(x, y, wide, high);
		g2.setComposite(comp_defo);
	}

	public void drawSelect(Graphics g, Container con, int x, int y, int width,
			int height, String strs[], boolean oks[], int now) {
		Color colors[] = new Color[strs.length];
		for (int i = 0; i < colors.length; i++)
			colors[i] = MenuItemColor;

		drawSelect(g, con, x, y, width, height, strs, oks, now, colors);
	}

	public void drawSelect(Graphics g, Container con, int x, int y, int width,
			int height, String strs[], boolean oks[], int now, Color cols[]) {
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		drawChoices(g, x, y, width, height, strs, oks, cols);
		drawNowDecide(g, x, y, width, height, strs.length, now);
		drawFrame(g, con, x, y, width, height);
		drawBorder(g, con, x, y, width, height, strs.length);
	}

	public void drawTable(Graphics g, Container con, int x, int y, int width,
			int height, String strs[], boolean border) {
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		boolean flags[] = new boolean[strs.length];
		for (int i = 0; i < flags.length; i++)
			flags[i] = true;

		drawChoices(g, x, y, width, height, strs, flags, Color.white);
		drawFrame(g, con, x, y, width, height);
		if (border)
			drawBorder(g, con, x, y, width, height, strs.length);
	}

	public void drawTable(Graphics g, Container con, int x, int y, int width,
			int height, String strs[]) {
		drawTable(g, con, x, y, width, height, strs, true);
	}

	public BufferedImage getWinTable(int width, int height, 
			Color start, Color end, boolean drawHeigth) {
		BufferedImage image = GraphicsUtils.createImage(width, height, true);
		Gradation gradation = Gradation.getInstance(start, end, width, height,
				125);
		Graphics g = image.getGraphics();
		if (drawHeigth) {
			gradation.drawHeight(g, 0, 0);
		} else {
			gradation.drawWidth(g, 0, 0);
		}
		drawFrame(g, null, 0, 0, width, height);
		g.dispose();

		return image;
	}
	
	public BufferedImage getTable(int width, int height) {
		BufferedImage image = GraphicsUtils.createImage(width, height, true);
		Graphics g = image.getGraphics();
		drawFrame(g, null, 0, 0, width, height);
		g.dispose();
		return image;
	}

	public void drawFrame(Graphics g, Container con, int x, int y, int width,
			int height) {
		BufferedImage corners[] = new BufferedImage[4];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = GraphicTool.get()
					.getBufferdImage((new StringBuilder("win")).append(i + 4)
							.toString());
		}
		int CornerSize = corners[0].getWidth();
		for (int a = 0; a < 4; a++) {
			BufferedImage img = null;
			int length = 0;
			int size = 0;
			int StartX = 0;
			int StartY = 0;
			switch (a) {
			case 0:
				length = width;
				img = GraphicTool.get().getBufferdImage("win0");
				size = img.getWidth();
				break;

			case 1:
				length = height;
				img = GraphicTool.get().getBufferdImage("win1");
				size = img.getHeight();
				break;

			case 2:
				length = width;
				img = GraphicTool.get().getBufferdImage("win2");
				size = img.getWidth();
				StartY = height - img.getHeight();
				break;

			case 3:
				length = height;
				img = GraphicTool.get().getBufferdImage("win3");
				size = img.getHeight();
				StartX = width - img.getWidth();
				break;
			}
			int finish = length - CornerSize;
			for (int i = CornerSize; i <= finish; i += size)
				if (a % 2 == 0)
					g.drawImage(img, x + i + StartX, y + StartY, con);
				else
					g.drawImage(img, x + StartX, y + i + StartY, con);

		}

		g.drawImage(corners[0], x, y, con);
		g.drawImage(corners[1], x, (y + height) - CornerSize, con);
		g.drawImage(corners[2], (x + width) - CornerSize, (y + height)
				- CornerSize, con);
		g.drawImage(corners[3], (x + width) - CornerSize, y, con);
	}

	private void drawBorder(Graphics g, Container con, int x, int y, int width,
			int height, int nums) {
		BufferedImage img = GraphicTool.get().getBufferdImage("win0");
		int size = img.getHeight();
		int length = img.getWidth();
		int bun = (int) Math.round((1.0D * (double) (height - size))
				/ (double) nums);
		for (int i = 1; i < nums; i++) {
			for (int j = 0; j <= width - size; j += length)
				g.drawImage(img, x + j, y + bun * i, con);

		}

	}

	public void drawHorizonLine(Graphics g, Container con, int x, int y,
			int width) {
		BufferedImage img = GraphicTool.get().getBufferdImage("win0");
		int length = img.getWidth();
		for (int j = 0; j <= width; j += length)
			g.drawImage(img, x + j, y, con);

	}

	private void drawChoices(Graphics g, int x, int y, int width, int height,
			String strs[], boolean oks[], Color col) {
		Color colors[] = new Color[strs.length];
		for (int i = 0; i < colors.length; i++)
			colors[i] = col;

		drawChoices(g, x, y, width, height, strs, oks, colors);
	}

	private void drawChoices(Graphics g, int x, int y, int width, int height,
			String strs[], boolean oks[], Color colors[]) {
		BufferedImage img = GraphicTool.get().getBufferdImage("win0");
		int size = img.getHeight();
		int bun = (int) Math.round((1.0D * (double) (height - size))
				/ (double) strs.length);
		for (int i = 0; i < strs.length; i++) {
			g.setFont(new Font("Dialog", 1, getFontSize(
					bun - size,
					(int) Math.round((1.0D * (double) (width - 2 * size))
							/ (double) strs[i].length()))));
			g.setColor(colors[i]);
			g.drawString(
					strs[i],
					x + size,
					(int) Math.round((double) (y + bun * (i + 1))
							- 0.10000000000000001D * (double) bun));
			if (!oks[i])
				setTransmission(g, x, y + bun * i, width, bun, Color.black,
						0.7F);
		}

	}

	private void drawNowDecide(Graphics g, int x, int y, int width, int height,
			int nums, int now) {
		if (now == -1) {
			return;
		} else {
			BufferedImage img = GraphicTool.get().getBufferdImage("win0");
			int size = img.getHeight();
			int bun = (int) Math.round((1.0D * (double) (height - size))
					/ (double) nums);
			setTransmission(g, x, y + bun * now, width, bun, Color.green, 0.5F);
			return;
		}
	}

	private int getFontSize(int h, int w) {
		if (h > w)
			return w - 3;
		else
			return h - 3;
	}

	public void changeMenuItemColor(Color col) {
		MenuItemColor = col;
	}

	public Color getMenuItemColor() {
		return MenuItemColor;
	}

	private Color MenuItemColor;
}
