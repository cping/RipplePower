package org.ripple.power.ui.projector.core.graphics.component;

import java.awt.event.MouseEvent;

import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.projector.action.sprite.Animation;
import org.ripple.power.ui.projector.core.graphics.LComponent;
import org.ripple.power.ui.projector.core.graphics.LContainer;

public class LPaper extends LContainer {

	private Animation animation = new Animation();

	public LPaper(LImage background, int x, int y) {
		super(x, y, background.getWidth(), background.getHeight());
		this.customRendering = true;
		this.setBackground(background);
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public LPaper(LImage background) {
		this(background, 0, 0);
	}

	public LPaper(String fileName, int x, int y) {
		this(new LImage(fileName), x, y);
	}

	public LPaper(String fileName) {
		this(fileName, 0, 0);
	}

	public LPaper(int x, int y, int w, int h) {
		this(new LImage(w < 1 ? w = 1 : w, h < 1 ? h = 1 : h, true), x, y);
	}

	public Animation getAnimation() {
		return this.animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void addAnimationFrame(String fileName, long timer) {
		animation.addFrame(fileName, timer);
	}

	public void addAnimationFrame(LImage image, long timer) {
		animation.addFrame(image.getBufferedImage(), timer);
	}

	public void doClick() {
	}

	public void downClick() {
	}

	public void upClick() {
	}

	protected void processTouchClicked() {
		if (this.input.getTouchReleased() == MouseEvent.BUTTON1) {
			this.doClick();
		}
	}

	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	protected void createCustomUI(LGraphics g, int x, int y, int w, int h) {
		if (visible) {
			if (animation.getSpriteImage() != null) {
				g.drawImage(animation.getSpriteImage().getLImage(), x, y);
			}
			if (x != 0 && y != 0) {
				g.translate(x, y);
				paint(g);
				g.translate(-x, -y);
			} else {
				paint(g);
			}
		}
	}

	public void paint(LGraphics g) {

	}

	public void update(long elapsedTime) {
		if (visible) {
			super.update(elapsedTime);
			animation.update(elapsedTime);
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
	}

	protected void processTouchPressed() {
		if (this.input.getTouchPressed() == MouseEvent.BUTTON1) {
			this.downClick();
		}
	}

	protected void processTouchReleased() {
		if (this.input.getTouchReleased() == MouseEvent.BUTTON1) {
			this.upClick();
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	protected void validateSize() {
		super.validateSize();
	}

	public void createUI(LGraphics g, int x, int y, LComponent component, LImage[] buttonImage) {

	}

	public String getUIName() {
		return "Paper";
	}

	public String toString() {
		return getUIName();
	}

}
