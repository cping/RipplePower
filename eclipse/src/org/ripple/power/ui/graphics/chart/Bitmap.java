package org.ripple.power.ui.graphics.chart;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;

class Bitmap {
	
	protected LImage bufferedImage;
	
	public static Bitmap createBitmap(int width,int height){
		return new Bitmap(width, height);
	}

	Bitmap(int width, int height) {
		this.bufferedImage = new LImage(width, height, true);
	}

	public void setBackgroundColor(int color) {
		LGraphics graphics = bufferedImage.getLGraphics();
		graphics.setColor(new LColor(color, true));
		graphics.fillRect(0, 0, bufferedImage.getWidth(),
				bufferedImage.getHeight());
		graphics.dispose();
	}

	public int getHeight() {
		return bufferedImage == null ? 0 : this.bufferedImage.getHeight();
	}

	public int getWidth() {
		return bufferedImage == null ? 0 : this.bufferedImage.getWidth();
	}

	public void recycle() {
	}

	public boolean isValid() {
		return true;
	}
}
