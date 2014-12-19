package org.ripple.power.ui.projector.action.sprite.effect;

import java.awt.Image;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.utils.GraphicsUtils;

public class PetalKernel implements IKernel {

	private boolean exist;

	private Image sakura;

	private double offsetX, offsetY, speed, x, y, width, height, sakuraWidth,
	sakuraHeight;

	public PetalKernel(int n, int w, int h) {
		sakura = GraphicsUtils.loadImage(LSystem.FRAMEWORK_IMG_NAME+("sakura_" + n + ".png")
				.intern());
		sakuraWidth = sakura.getWidth(null);
		sakuraHeight = sakura.getHeight(null);
		width = w;
		height = h;
		offsetX = 0;
		offsetY = n * 0.6 + 1.9 + Math.random() * 0.2;
		speed = Math.random();
	}

	public void make() {
		exist = true;
		x = Math.random() * width;
		y = -sakuraHeight;
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
			if(y >= height) {
				y = -(int) (LSystem.random.nextFloat() * 1)
				- sakuraHeight;
				x =  (int) (LSystem.random.nextFloat() * (width - 1));
			}
		}
	}

	public void draw(LGraphics g) {
		if (exist) {
			g.drawImage(sakura, (int) x, (int) y);
		}
	}

	public Image getSnow() {
		return sakura;
	}

	public double getSakuraHeight() {
		return sakuraHeight;
	}

	public double getSakuraWidth() {
		return sakuraWidth;
	}

}
