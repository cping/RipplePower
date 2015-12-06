package org.ripple.power.ui.projector.action.sprite.effect;

import java.awt.Image;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.utils.GraphicsUtils;

public class SnowKernel implements IKernel {

	private boolean exist;

	private Image snow;

	private double offsetX, offsetY, speed, x, y, width, height, snowWidth,
			snowHeight;

	public SnowKernel(int n, int w, int h) {
		snow = GraphicsUtils.loadImage(LSystem.FRAMEWORK_IMG_NAME
				+ ("snow_" + n + ".png").intern());
		snowWidth = snow.getWidth(null);
		snowHeight = snow.getHeight(null);
		width = w;
		height = h;
		offsetX = 0;
		offsetY = n * 0.6 + 1.9 + Math.random() * 0.2;
		speed = Math.random();
	}

	public void make() {
		exist = true;
		x = Math.random() * width;
		y = -snowHeight;
	}

	public void move() {
		if (!exist) {
			if (Math.random() < 0.002) {
				make();
			}
		} else {
			x += offsetX;
			y += offsetY;
			offsetX += speed;
			speed += (Math.random() - 0.5) * 0.3;
			if (offsetX >= 1.5) {
				offsetX = 1.5;
			}
			if (offsetX <= -1.5) {
				offsetX = -1.5;
			}
			if (speed >= 0.2) {
				speed = 0.2;
			}
			if (speed <= -0.2) {
				speed = -0.2;
			}
			if (y >= height) {
				y = -snowHeight;
				x = Math.random() * width;
			}
		}
	}

	public void draw(LGraphics g) {
		if (exist) {
			g.drawImage(snow, (int) x, (int) y);
		}
	}

	public Image getSnow() {
		return snow;
	}

	public double getSnowHeight() {
		return snowHeight;
	}

	public double getSnowWidth() {
		return snowWidth;
	}

}
