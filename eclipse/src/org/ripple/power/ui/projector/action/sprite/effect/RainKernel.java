package org.ripple.power.ui.projector.action.sprite.effect;

import java.awt.Image;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.utils.GraphicsUtils;

public class RainKernel implements IKernel {

	private boolean exist;

	private Image rain;

	private double offsetX, offsetY, x, y, width, height, rainWidth,
			rainHeight;

	public RainKernel(int n, int w, int h) {
		rain = GraphicsUtils.loadImage(LSystem.FRAMEWORK_IMG_NAME
				+ ("rain_" + n + ".png").intern());
		rainWidth = rain.getWidth(null);
		rainHeight = rain.getHeight(null);
		width = w;
		height = h;
		offsetX = 0;
		offsetY = (5 - n) * 30 + 75 + Math.random() * 15;
	}

	public void make() {
		exist = true;
		x = Math.random() * width;
		y = -rainHeight;
	}

	public void move() {
		if (!exist) {
			if (Math.random() < 0.002)
				make();
		} else {
			x += offsetX;
			y += offsetY;
			if (y >= height) {
				x = Math.random() * width;
				y = -rainHeight * Math.random();
			}
		}
	}

	public void draw(LGraphics g) {
		if (exist) {
			g.drawImage(rain, (int) x, (int) y);
		}
	}

	public Image getRain() {
		return rain;
	}

	public double getRainHeight() {
		return rainHeight;
	}

	public double getRainWidth() {
		return rainWidth;
	}

}
