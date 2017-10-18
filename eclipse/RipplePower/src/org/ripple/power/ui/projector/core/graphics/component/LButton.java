package org.ripple.power.ui.projector.core.graphics.component;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.ripple.power.ui.graphics.LFont;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.projector.core.graphics.LComponent;
import org.ripple.power.ui.projector.core.graphics.filter.ImageFilterFactory;
import org.ripple.power.utils.GraphicsUtils;

public class LButton extends LComponent {

	private String text = null;

	private boolean over, pressed, exception;

	private int pressedTime, offsetLeft, offsetTop;

	private LFont font = LFont.getDefaultFont();

	private Color fontColor = Color.white;

	public LButton(String fileName) {
		this(fileName, null, 0, 0);
	}

	public LButton(String fileName, String text, int x, int y) {
		this(new LImage(fileName), text, x, y);
	}

	public LButton(LImage img, String text, int x, int y) {
		this(img, text, img.getWidth(), img.getHeight(), x, y);
	}

	public LButton(String fileName, int row, int col) {
		this(fileName, null, row, col, 0, 0);
	}

	public LButton(String fileName, String text, int row, int col, int x, int y) {
		this(new LImage(fileName), text, row, col, x, y);
	}

	public LButton(LImage img, String text, int row, int col, int x, int y) {
		this(GraphicsUtils.getSplitLImages(img, row, col), text, row, col, x, y);
	}

	public LButton(LImage[] img, String text, int row, int col, int x, int y) {
		super(x, y, row, col);
		this.setImages(img);
		this.text = text;
	}

	public LButton(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.text = text;
	}

	public void setImages(LImage[] images) {
		LImage[] buttons = new LImage[4];
		if (images != null) {
			int size = images.length;
			switch (size) {
			case 1:
				BufferedImage image = ImageFilterFactory.getGray(images[0].getBufferedImage());
				buttons[0] = new LImage(image);
				if (images[0].getBufferedImage() != image) {
					images[0].dispose();
					images[0] = null;
				}
				images[0] = buttons[0];
				buttons[1] = images[0];
				buttons[2] = images[0];
				buttons[3] = images[0];
				break;
			case 2:
				buttons[0] = images[0];
				buttons[1] = images[1];
				buttons[2] = images[0];
				buttons[3] = images[0];
				break;
			case 3:
				buttons[0] = images[0];
				buttons[1] = images[1];
				buttons[2] = images[2];
				buttons[3] = images[0];
				break;
			case 4:
				buttons = images;
				break;
			default:
				exception = true;
				break;
			}
		}
		if (!exception) {
			this.setImageUI(buttons, true);
		}

	}

	public void createUI(LGraphics g, int x, int y, LComponent component, LImage[] buttonImage) {
		LButton button = (LButton) component;
		if (buttonImage != null) {
			if (!button.isEnabled()) {
				g.drawImage(buttonImage[3], x, y);
			} else if (button.isTouchPressed()) {
				g.drawImage(buttonImage[2], x, y);
			} else if (button.isTouchOver()) {
				g.drawImage(buttonImage[1], x, y);
			} else {
				g.drawImage(buttonImage[0], x, y);
			}
		}
		if (text != null) {
			LFont old = g.getLFont();
			Color color = g.getColor();
			g.setFont(font);
			g.setColor(fontColor);
			g.drawString(text, x + button.getOffsetLeft() + (button.getWidth() - font.stringWidth(text)) / 2,
					y + button.getOffsetTop() + (button.getHeight() - font.getLineHeight()) / 2 + font.getLineHeight());
			g.setFont(old);
			g.setColor(color);
		}
	}

	public void update(long timer) {
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			this.pressed = false;
		}
	}

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String st) {
		this.text = st;
	}

	protected void processTouchDragged() {
		if (this.input.getKeyPressed() == MouseEvent.BUTTON1) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(), this.input.getTouchY());
		}
	}

	/**
	 * 处理点击事件（请重载实现）
	 * 
	 */
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

	protected void processTouchPressed() {
		if (this.input.getTouchPressed() == MouseEvent.BUTTON1) {
			this.downClick();
			this.pressed = true;
		}
	}

	protected void processTouchReleased() {
		if (this.input.getTouchReleased() == MouseEvent.BUTTON1) {
			this.upClick();
			this.pressed = false;
		}
	}

	protected void processTouchEntered() {
		this.over = true;
	}

	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == KeyEvent.VK_ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected() && this.input.getKeyReleased() == KeyEvent.VK_ENTER) {
			this.pressed = false;
		}
	}

	public boolean isException() {
		return exception;
	}

	public String getUIName() {
		return "Button";
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public int getOffsetLeft() {
		return offsetLeft;
	}

	public void setOffsetLeft(int offsetLeft) {
		this.offsetLeft = offsetLeft;
	}

	public int getOffsetTop() {
		return offsetTop;
	}

	public void setOffsetTop(int offsetTop) {
		this.offsetTop = offsetTop;
	}

}
