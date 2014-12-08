package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;

import org.ripple.power.ui.graphics.LColor;

public class UIConfig {
	
	private static Font basicFont = null;

	private static LColor brandColor = new LColor(86,61,124);

	private static boolean cursorOff = false;

	private static Image defaultAppIcon = null;

	private static Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			Toolkit.getDefaultToolkit().createImage(new byte[0]), new Point(0, 0), "blank cursor");

	public static boolean getCursorOff() {
		return UIConfig.cursorOff;
	}

	public static void setCursorOff(boolean cursorOff) {
		UIConfig.cursorOff = cursorOff;
	}

	public static Cursor getTransparentCursor() {
		return transparentCursor;
	}

	public static Font getBasicFont() {
		if (basicFont == null) {
			basicFont = new Font("Dialog", Font.PLAIN, 14);
		}
		return basicFont;
	}

	public static void setBasicFont(Font basicFont) {
		UIConfig.basicFont = basicFont;
	}

	public static Image getDefaultAppIcon() {
		if (defaultAppIcon == null) {
			defaultAppIcon = buildAppIconImage();
		}
		return defaultAppIcon;
	}

	public static void setDefaultAppIcon(Image defaultAppIcon) {
		UIConfig.defaultAppIcon = defaultAppIcon;
	}

	public static LColor getBrandColor() {
		return brandColor;
	}

	public static void setBrandColor(LColor color) {
		UIConfig.brandColor = color;
	}

	public static void loadConfig() {
		if (basicFont == null) {
			basicFont = new Font("Dialog", Font.PLAIN, 14);
		}
		UIManager.put("Button.font", basicFont);
		UIManager.put("ToggleButton.font", basicFont);
		UIManager.put("RadioButton.font", basicFont);
		UIManager.put("CheckBox.font", basicFont);
		UIManager.put("ColorChooser.font", basicFont);
		UIManager.put("ComboBox.font", basicFont);
		UIManager.put("Label.font", basicFont);
		UIManager.put("List.font", basicFont);
		UIManager.put("MenuBar.font", basicFont);
		UIManager.put("MenuItem.font", basicFont);
		UIManager.put("RadioButtonMenuItem.font", basicFont);
		UIManager.put("CheckBoxMenuItem.font", basicFont);
		UIManager.put("Menu.font", basicFont);
		UIManager.put("PopupMenu.font", basicFont);
		UIManager.put("OptionPane.font", basicFont);
		UIManager.put("Panel.font", basicFont);
		UIManager.put("ProgressBar.font", basicFont);
		UIManager.put("ScrollPane.font", basicFont);
		UIManager.put("Viewport.font", basicFont);
		UIManager.put("TabbedPane.font", basicFont);
		UIManager.put("Table.font", basicFont);
		UIManager.put("TableHeader.font", basicFont);
		UIManager.put("TextField.font", basicFont);
		UIManager.put("PasswordField.font", basicFont);
		UIManager.put("TextArea.font", basicFont);
		UIManager.put("TextPane.font", basicFont);
		UIManager.put("EditorPane.font", basicFont);
		UIManager.put("TitledBorder.font", basicFont);
		UIManager.put("ToolBar.font", basicFont);
		UIManager.put("ToolTip.font", basicFont);
		UIManager.put("Tree.font", basicFont);
	}

	private static Image buildAppIconImage() {
		String icon = "R";
		BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setColor(brandColor);
		g2.fillRoundRect(0, 0, 40, 40, 10, 10);
		g2.setColor(Color.WHITE);
		Font font = new Font("Arial", Font.PLAIN, 24);
		g2.setFont(font);
		LineMetrics lm = font.getLineMetrics(icon, g2.getFontRenderContext());
		int iconWidth = g2.getFontMetrics().stringWidth(icon);
		int iconHeight = (int) lm.getHeight();
		int iconBaseline = (int) (lm.getHeight() - lm.getLeading() - lm.getDescent());
		g2.drawString(icon, (40 - iconWidth) / 2, (40 - iconHeight) / 2 + iconBaseline);
		g2.dispose();
		return image;
	}
}
