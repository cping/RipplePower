package org.ripple.power.helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.config.Loop;
import org.ripple.power.config.Model;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LFont;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.ui.view.RPPushTool;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class HelperDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final StringBuilder message = new StringBuilder();

	private LFont deffont = LFont.getFont(Font.SANS_SERIF, 1, 20);

	class HelperMessage extends Loop {

		@Override
		public void runTaskTimer(LTimerContext context) {
			update((int) context.millisSleepTime);
			repaint();
			if (HelperDialog.this.finished) {
				this.destroy();
			}
		}

		@Override
		public Updateable main() {
			return new Updateable() {

				@Override
				public void action(Object o) {
					mainLoop();
				}
			};
		}

	}

	private static RPPushTool instance = null;

	private static RPPushTool load() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = null;
		if (LSystem.applicationMain != null) {
			screenInsets = Toolkit.getDefaultToolkit()
					.getScreenInsets(LSystem.applicationMain.getGraphicsConfiguration());
		} else {
			screenInsets = new Insets(0, 0, 480, 320);
		}
		final HelperDialog helper = new HelperDialog();
		helper.setSize(new Dimension(GraphicTool.Width_MaidSystem, GraphicTool.Height_MaidSystem));
		helper.setPreferredSize(new Dimension(GraphicTool.Width_MaidSystem, GraphicTool.Height_MaidSystem));
		helper.setBackground(LColor.black);
		RPPushTool rpp = RPPushTool.pop(new Point((size.width - GraphicTool.Width_MaidSystem) / 2, size.getHeight()),
				(int) (screenInsets.bottom + helper.getHeight() + 90),
				LangConfig.get(HelperDialog.class, "ripple_wizard", "Ripple Wizard"), helper);
		rpp.obj = helper;
		rpp.setListener(new RPPushTool.ClosedListener() {

			@Override
			public void closed() {
				if (helper.messageLoop != null) {
					helper.messageLoop.destroy();
				}

			}
		});
		return rpp;
	}

	private static int MESSAGE_TYPE_INTERVAL = 50;

	private static int MESSAGE_PAGE_BLINK_TIME = 300;

	private int messageIndex = 0;

	private HelperMessage messageLoop;

	protected List<String> lines;

	protected List<String> wrapMessage(String text, LFont font, int width) {
		List<String> list = new ArrayList<String>();

		if (text == null) {
			return list;
		}

		char c1 = '〜';
		char c2 = 65374;
		String str = text.replace(c1, c2);
		String line = "";

		// other char flag
		char[] wrapchars = { '\u3002', '\u3001', '\uff0c', '\uff0e', '\u300d', '\uff3d', '\u3011', '\u300f', '\u30fc',
				'\uff5e', '\uff09', '\u3041', '\u3043', '\u3045', '\u3047', '\u3049', '\u30a1', '\u30a3', '\u30a5',
				'\u30a7', '\u30a9', '\u30c3', '\u30e3', '\u30e5', '\u30e7', '\u30ee', '\u308e', '\u3083', '\u3085',
				'\u3087', '\u3063', '\u2026', '\uff0d', '\uff01', '\uff1f' };

		int i = 0;
		while (i <= str.length()) {
			if (i == str.length()) {
				list.add(line);
				break;
			}

			char c = str.charAt(i);

			if ((c == '\n') || (font.stringWidth(line + c) > width)) {
				line = str.substring(0, i);

				for (int j = 0; j < wrapchars.length; j++) {
					if (c == wrapchars[j]) {
						int delta = font.stringWidth(line + c) - width;
						if (delta < 15) {
							line = str.substring(0, ++i);
							break;
						}
					}
				}
				i += (c == '\n' ? 1 : 0);
				list.add(line);
				line = "";
				str = str.substring(i);
				i = 0;
			} else {
				line = line + c;
				i++;
			}
		}
		return list;
	}

	private Image offscreenImg;

	Image[] faceImage;
	private String messageString;
	int fx = 126;
	int fy = 2;
	int fwidth = 756;
	int fheight = 150;
	BufferedImage _backimage;
	BufferedImage _faceimage;
	final int idx = 7;
	protected int typeDelayTime;
	protected int renderRow;
	protected int renderCol;
	private boolean stopMessage;

	private boolean noMessage, finished;
	private int pageBlinkTime;

	public void reset() {
		this.renderCol = 0;
		this.renderRow = 0;
		this.finished = false;
		this.lines = wrapMessage(getMessage(), this.deffont, this.fwidth - (this.deffont.getSize() * 2));
		this.messageIndex = (this.renderRow = this.renderCol = 0);
		this.typeDelayTime = MESSAGE_TYPE_INTERVAL;
		this.pageBlinkTime = MESSAGE_PAGE_BLINK_TIME;
		this.finished = this.noMessage;
		this.stopMessage = false;
		this.setIndex(0);
		if (messageLoop == null) {
			messageLoop = new HelperMessage();
			messageLoop.loop();
		} else {
			if (messageLoop.isRunning()) {
				messageLoop.resume();
			} else {
				messageLoop = new HelperMessage();
				messageLoop.loop();
			}
		}
	}

	public void setBlankMessage() {
		this.lines = new ArrayList<String>();
	}

	protected void showAll() {
		if (this.lines.isEmpty()) {
			this.renderRow = (this.renderCol = 0);
		} else {
			this.renderRow = (this.lines.size() - 1);
			this.renderCol = ((String) this.lines.get(this.renderRow)).length();
			this.finished = true;
		}
	}

	public void nextIndex() {
		setIndex(++this.messageIndex);
	}

	public void setIndex(int index) {
		this.messageIndex = index;
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

	public static boolean isSystemVisible() {
		if (instance != null) {
			return instance.isVisible();
		}
		return false;
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
			if (instance.isVisible() && instance.getOpacity() == 1f && !instance.isClose()) {
				SwingUtils.fadeOut(instance.getDialog(), false);
			}
		}
	}

	public static void showSystem() {
		if (instance != null) {
			if (!instance.isVisible() && instance.getOpacity() == 0f && !instance.isClose()) {
				SwingUtils.fadeIn(instance.getDialog());
			}
		}
	}

	HelperDialog() {
		faceImage = GraphicsUtils.getSplitImages("icons/monster.png", 96, 96);
		GraphicTool tools = new GraphicTool();
		_backimage = tools.getWinTable(fwidth, fheight, Color.white, UIConfig.background, true);
		_faceimage = tools.getTable(faceImage[idx].getWidth(this), faceImage[idx].getHeight(this));
	}

	private void update(int delta) {
		if (!this.noMessage) {
			if ((!this.stopMessage) && (!this.lines.isEmpty())) {
				this.typeDelayTime -= delta;
				updateType();
			}
			if (this.finished) {
				this.pageBlinkTime += delta;
			}
		}
	}

	protected void updateType() {
		if ((this.typeDelayTime <= 0) && (!this.finished)) {
			this.typeDelayTime = MESSAGE_TYPE_INTERVAL;
			if (this.renderCol > ((String) this.lines.get(this.renderRow)).length() - 1) {
				if (this.renderRow >= this.lines.size() - 1) {
					this.finished = true;
					this.pageBlinkTime = MESSAGE_PAGE_BLINK_TIME;
				} else {
					this.renderRow += 1;
					this.renderCol = 0;
				}
			} else {
				this.renderCol += 1;
			}
		}
	}

	public void setMessage(String mes) {
		this.messageString = mes;
		this.reset();
	}

	public String getMessage() {
		return messageString;
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
			g.drawImage(offscreenImg, 0, 0, this);
		}
	}

	public void drawFace(Graphics g, int x, int y) {
		if (faceImage[0] != null) {
			g.drawImage(faceImage[idx], x, y, this);
			g.drawImage(_faceimage, x, y, this);
		}
	}

	public void pauseMessage() {
		this.stopMessage = true;
	}

	public void resumeMessage() {
		this.stopMessage = false;
	}

	private void draw(Graphics g) {
		drawFace(g, 18, fy + 24);
		g.drawImage(_backimage, fx, fy, this);
		if (messageString != null) {
			g.setColor(Color.white);
			g.setFont(deffont.getFont());
			GraphicsUtils.setAntialias(g, true);
			if (!finished) {
				message.delete(0, message.length());
				if (!this.lines.isEmpty()) {
					for (int i = 0; i < this.renderRow + 1; i++) {
						String line = (String) this.lines.get(i);
						int len = 0;
						if (i < this.renderRow) {
							len = line.length();
						} else {
							len = this.renderCol;
						}
						if (line.length() < len) {
							len = line.length();
						}
						String t = line.substring(0, len);
						if (t.length() != 0) {
							if (len == line.length()) {
								message.append(t + LSystem.LS);
							} else {
								message.append(t);
							}
						}
					}
				}
				if (this.finished) {
					if (this.pageBlinkTime > MESSAGE_PAGE_BLINK_TIME) {
						this.pageBlinkTime = 0;
					}
				}
				drawLineString(g, message.toString(), (int) Math.round(fx + 0.029D * fwidth),
						(int) Math.round(fy + 0.29D * fheight));
			} else {
				drawLineString(g, message.toString(), (int) Math.round(fx + 0.029D * fwidth),
						(int) Math.round(fy + 0.29D * fheight));
			}
		}
	}

	private void drawLineString(Graphics g, String message, int x, int y) {
		if (message != null) {
			String[] lineMessages = StringUtils.split(message, LSystem.LS);
			int size = g.getFontMetrics().getHeight();
			for (int i = 0; i < lineMessages.length; i++) {
				g.drawString(lineMessages[i], x, y + (size * i));
			}
		}
	}

	public static void setHelperMessage(RPPushTool rpp, String message) {
		if ((rpp.obj != null) && (rpp.obj instanceof HelperDialog)) {
			HelperDialog dialog = (HelperDialog) rpp.obj;
			dialog.setMessage(message);
		}
	}

	public static void setSystemHelperMessage(String message) {
		if (LSystem.current == Model.Ripple) {
			if (HelperDialog.isSystemVisible()) {
				RPPushTool rpp = HelperDialog.get();
				HelperDialog.setHelperMessage(rpp, message);
			}
		}
	}

}
