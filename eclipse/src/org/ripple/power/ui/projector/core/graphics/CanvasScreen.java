package org.ripple.power.ui.projector.core.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;

public abstract class CanvasScreen extends Screen  {

	public static final int UP_PRESSED = 0x0002;

	public static final int DOWN_PRESSED = 0x0040;

	public static final int LEFT_PRESSED = 0x0004;

	public static final int RIGHT_PRESSED = 0x0020;

	public static final int FIRE_PRESSED = 0x0100;

	public static final int GAME_A_PRESSED = 0x0200;

	public static final int GAME_B_PRESSED = 0x0400;

	public static final int GAME_C_PRESSED = 0x0800;

	public static final int GAME_D_PRESSED = 0x1000;

	private int keyStates;

	private int releasedKeys;

	private LImage bufferedImage;

	private BufferedImage grapImage;

	private LGraphics screenGraphics;

	private int offsetX, offsetY, clipX, clipY, clipWidth, clipHeight;

	private boolean setClip, fullSize, updateFlag, moveFlag, overFlag;

	private int nowWidth, nowHeight, tmpWidth, tmpHeight;

	final static private int MAX_BUFFER_FPS = 15;

	public CanvasScreen() {
		LSystem.AUTO_REPAINT = false;
		this
				.setFPS(getMaxFPS() > MAX_BUFFER_FPS ? MAX_BUFFER_FPS
						: getMaxFPS());
		this.setRepaintMode(SCREEN_NOT_REPAINT);
		this.bufferedImage = LImage.createImage(getWidth(), getHeight(), false);
		this.nowWidth = getWidth();
		this.nowHeight = getHeight();
		this.screenGraphics = bufferedImage.getLGraphics();
		this.grapImage = bufferedImage.getBufferedImage();
	}

	public CanvasScreen(int nw, int nh, int w, int h) {
		LSystem.AUTO_REPAINT = false;
		this
				.setFPS(getMaxFPS() > MAX_BUFFER_FPS ? MAX_BUFFER_FPS
						: getMaxFPS());
		this.setRepaintMode(SCREEN_NOT_REPAINT);
		this.bufferedImage = LImage.createImage(nw, nh, false);
		this.screenGraphics = bufferedImage.getLGraphics();
		this.grapImage = bufferedImage.getBufferedImage();
		this.setSize(w, h);
	}

	public CanvasScreen(int w, int h) {
		LSystem.AUTO_REPAINT = false;
		this
				.setFPS(getMaxFPS() > MAX_BUFFER_FPS ? MAX_BUFFER_FPS
						: getMaxFPS());
		this.nowWidth = w;
		this.nowHeight = h;
		this.updateFlag = true;
		this.setRepaintMode(SCREEN_NOT_REPAINT);
		this.bufferedImage = LImage.createImage(w, h, false);
		this.screenGraphics = bufferedImage.getLGraphics();
		this.grapImage = bufferedImage.getBufferedImage();
	}

	public FontMetrics getFontMetrics(Font font) {
		return screenGraphics.getFontMetrics(font);
	}

	public FontMetrics getFontMetrics() {
		return screenGraphics.getFontMetrics();
	}

	public void move(int x, int y) {
		if (x > 0 || y > 0) {
			moveFlag = true;
		} else {
			moveFlag = false;
		}
		offsetX = x;
		offsetY = y;
	}

	public void setSize(int w, int h) {
		this.nowWidth = w;
		this.nowHeight = h;
		this.updateFlag = true;
		this.fullSize = true;
	}

	public void dispose() {
		super.dispose();
		this.overFlag = true;
		if (bufferedImage != null) {
			bufferedImage.dispose();
			bufferedImage = null;
		}
		if (screenGraphics != null) {
			screenGraphics.dispose();
			screenGraphics = null;
		}
		if (grapImage != null) {
			grapImage.flush();
			grapImage = null;
		}
	}

	public int getKeyStates() {
		int states = this.keyStates;
		this.keyStates &= ~this.releasedKeys;
		this.releasedKeys = 0;
		return states;
	}

	public int getCurrentWidth() {
		return this.nowWidth;
	}

	public int getCurrentHeight() {
		return this.nowHeight;
	}

	public void flushGraphics(int x, int y, int width, int height) {
		this.setClip = true;
		this.clipX = x;
		this.clipY = y;
		this.clipWidth = width;
		this.clipHeight = height;
		this.repaint();
	}

	public void flushGraphics() {
		this.setClip = true;
		this.clipX = 0;
		this.clipY = 0;
		this.clipWidth = nowWidth;
		this.clipHeight = nowHeight;
		this.repaint();
	}

	public LGraphics getGraphics() {
		return screenGraphics;
	}

	public void setFullScreenMode(boolean full) {
		if (full) {
			this.fullSize = full;
			this.tmpWidth = nowWidth;
			this.tmpHeight = nowHeight;
			this.nowWidth = getWidth();
			this.nowHeight = getHeight();
		} else {
			this.fullSize = full;
			this.nowWidth = tmpWidth;
			this.nowHeight = tmpHeight;
		}
	}

	public boolean isFullScreenMode() {
		return this.fullSize;
	}

	public synchronized void repaint() {
		if (!overFlag) {
			if (this.setClip) {
				screenGraphics.clipRect(this.clipX, this.clipY, this.clipWidth,
						this.clipHeight);
				this.setClip = false;
			}
			paint(screenGraphics);
			if (fullSize) {
				LSystem.repaintFull(grapImage, nowWidth, nowHeight);
			} else if (moveFlag) {
				LSystem.repaintLocation(grapImage, offsetX, offsetY);
			} else {
				if (updateFlag) {
					LSystem.repaint(grapImage, nowWidth, nowHeight);
				} else {
					LSystem.repaint(grapImage);
				}
			}
		}
	}

	public void repaint(int x, int y, int width, int height) {
		this.setClip = true;
		this.clipX = x;
		this.clipY = y;
		this.clipWidth = width;
		this.clipHeight = height;
		repaint();
	}

	public abstract void keyPressed(int keyCode);

	public abstract void keyReleased(int keyCode);

	public abstract void pointerReleased(double x, double y);

	public void onTouchUp(LTouch e) {
		pointerReleased(e.getX(), e.getY());
	}

	public abstract void pointerPressed(double x, double y);

	public void onTouchDown(LTouch e) {
		pointerPressed(e.getX(), e.getY());
	}

	public abstract void pointerMove(double x, double y);

	public void onTouchMove(LTouch e) {
		pointerMove(e.getX(), e.getY());
	}

	public void exitGame() {
		System.exit(0);
	}

	public abstract void paint(LGraphics g);

	final public void draw(LGraphics g) {

	}

}
