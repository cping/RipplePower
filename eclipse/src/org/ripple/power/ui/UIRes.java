package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.UIResource;

import org.ripple.power.collection.ArrayByte;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.utils.GraphicsUtils;
import org.spongycastle.util.encoders.Hex;

public class UIRes {

	private static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

	public static final String TABLE_TAG_START = "<table border='0' cellspacing='0' cellpadding='2'>";

	public static final String TABLE_TAG_END = "</table>";

	public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	public static final Dimension FORM_BUTTON_SIZE = new Dimension(100, 25);

	private static Object dialogReturnValue;
	private static Color defaultInactiveBackgroundColour;

	private static Icon checkBoxMenuItemIcon;

	private static Icon expandedTreeIcon;

	private static Icon collapsedTreeIcon;

	private final static LColor fontColorTitle = LColor.black;

	private final static LColor fontColor = new LColor(
			LColor.lightSkyBlue.darker());

	private final static BufferedImage icon;

	public final static ImageIcon postIcon;

	public final static ImageIcon exitIcon;

	public final static String PATH = "res/";

	private final static Map<String, ImageIcon> imageIcons = new HashMap<String, ImageIcon>();

	private static ClassLoader classLoader;

	static {
		try {
			classLoader = UIRes.class.getClassLoader();
		} catch (Exception e) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		icon = LImage.createImage("icons/ripple.png").getBufferedImage();
		postIcon = new ImageIcon(new LImage("icons/post.png").scaledInstance(
				48, 48).getBufferedImage());
		exitIcon = new ImageIcon(new LImage("icons/down.png").scaledInstance(
				48, 48).getBufferedImage());
	}


	public static BufferedImage getIcon() {
		return icon;
	}

	private static Font _font;

	public static Font getFont() {
		if (_font == null) {
			_font = GraphicsUtils.getFont(14);
		}
		return _font;
	}

	public static Font getFont(String fontName, int type, int style, int size) {
		try {
			Font font = Font.createFont(type, UIRes.getStream(fontName));
			font = font.deriveFont(style, size);
			final GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Font(Font.DIALOG, style, size);
	}
	
	public static void addPopMenu(final JPopupMenu menu, final String name,
			final Updateable update) {
		JMenuItem tempMenu = new JMenuItem(name);
		tempMenu.setFont(getFont());
		tempMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (update != null) {
					update.action(e);
				}
			}
		});
		menu.add(tempMenu);
	}

	public static ImageIcon getImage(String path) {
		path = computePath(path);
		if (!imageIcons.containsKey(path)) {
			URL url = classLoader.getResource(path);
			if (url == null) {
				throw new RuntimeException("File not found: " + path);
			}
			imageIcons.put(path, new ImageIcon(url));
		}

		return imageIcons.get(path);
	}

	public static ArrayByte getDataSource(String path) throws IOException {
		return new ArrayByte(getStream(path), ArrayByte.BIG_ENDIAN);
	}

	public static InputStream getStream(String path) throws IOException {
		path = computePath(path);
		InputStream is = classLoader.getResourceAsStream(path);
		if (is == null) {
			File file = new File(path);
			if (file.exists()) {
				return new BufferedInputStream(new FileInputStream(file));
			}
			throw new IOException("File not found: " + path);
		}
		return is;
	}

	public static URL getUrl(String path) {
		path = computePath(path);
		URL url = classLoader.getResource(path);
		if (url == null) {
			throw new RuntimeException("File not found: " + path);
		}
		return url;
	}

	private static String computePath(String path) {
		if (path.startsWith("/")) {
			return path;
		}
		return PATH + path;
	}

	public static void addStyle(JTextField textField, String labelName) {
		addStyle(textField, labelName, true);
	}

	public static void addStyle(JTextField textField, String labelName,
			boolean bottom) {
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
		titled.setTitleFont(GraphicsUtils.getFont("Verdana", 0, 13));
		titled.setTitleColor(fontColorTitle);
		Border empty = null;
		if (bottom) {
			empty = new EmptyBorder(5, 8, 5, 8);
		} else {
			empty = new EmptyBorder(5, 8, 0, 8);
		}
		CompoundBorder border = new CompoundBorder(titled, empty);
		textField.setBorder(border);
		textField.setForeground(fontColor);
		textField.setFont(GraphicsUtils.getFont("Monospaced", 0, 13));
	}

	public static void addStyle(JComboBox<Object> textField, String labelName) {
		Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
		titled.setTitleFont(GraphicsUtils.getFont("Verdana", 0, 13));
		titled.setTitleColor(fontColorTitle);
		Border empty = new EmptyBorder(0, 8, 0, 8);
		CompoundBorder border = new CompoundBorder(titled, empty);
		textField.setBorder(border);
		textField.setForeground(fontColor);
		textField.setFont(GraphicsUtils.getFont("Monospaced", 0, 13));
	}

	public static void addStyle(JTextArea textArea, String labelName,
			boolean isBorder) {
		Border border = null;
		if (isBorder) {
			Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
			TitledBorder titled = BorderFactory.createTitledBorder(line,
					labelName);
			titled.setTitleFont(GraphicsUtils.getFont("Verdana", 0, 13));
			titled.setTitleColor(fontColorTitle);
		}
		textArea.setBorder(border);
		textArea.setForeground(fontColor);
		textArea.setFont(GraphicsUtils.getFont("Monospaced", 0, 13));
	}

	public static void addStyle(JScrollPane jScrollPane, String labelName) {
		addStyle(jScrollPane, labelName, false);
	}

	public static void addStyle(JScrollPane jScrollPane, String labelName,
			boolean bottom) {
		Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
		titled.setTitleFont(GraphicsUtils.getFont("Verdana", 0, 13));
		titled.setTitleColor(fontColorTitle);
		Border empty = null;
		if (bottom) {
			empty = new EmptyBorder(5, 8, 5, 8);
		} else {
			empty = new EmptyBorder(5, 8, 0, 8);
		}
		CompoundBorder border = new CompoundBorder(titled, empty);
		jScrollPane.setBorder(border);
		jScrollPane.setForeground(fontColor);
		jScrollPane.setBackground(Color.WHITE);
		jScrollPane.setFont(GraphicsUtils.getFont("Monospaced", 0, 13));
		jScrollPane.setHorizontalScrollBar(null);
	}

	public static void addStyle(JTable jTable) {
		jTable.setForeground(fontColor);
		jTable.setBackground(Color.WHITE);
		jTable.setFont(GraphicsUtils.getFont("Monospaced", 0, 13));
	}

	public static String getHexStyledText(byte[] data) {
		String[] dataHex = Hex.toHexString(data).split("(?<=\\G.{2})");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < dataHex.length; ++i) {
			sb.append(dataHex[i]).append(" ");
			if ((i + 1) % 8 == 0 && i != 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static Color getDefaultBorderColour() {
		return UIManager.getColor("controlShadow");
	}

	public static Color getDefaultInactiveBackgroundColour() {
		if (defaultInactiveBackgroundColour == null) {
			defaultInactiveBackgroundColour = UIManager.getColor("control");
		}
		return defaultInactiveBackgroundColour;
	}

	public static Color getInverse(Color colour) {
		int red = 255 - colour.getRed();
		int green = 255 - colour.getGreen();
		int blue = 255 - colour.getBlue();
		return new Color(red, green, blue);
	}

	public static Color getDarker(Color color, double factor) {
		return new Color(Math.max((int) (color.getRed() * factor), 0),
				Math.max((int) (color.getGreen() * factor), 0), Math.max(
						(int) (color.getBlue() * factor), 0));
	}

	public static Color getBrighter(Color color, double factor) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;

		return new Color(Math.min((int) (r / factor), 255), Math.min(
				(int) (g / factor), 255), Math.min((int) (b / factor), 255));
	}

	static Icon getCheckBoxMenuItemIcon() {
		if (checkBoxMenuItemIcon == null) {
			checkBoxMenuItemIcon = new CheckBoxMenuItemIcon();
		}
		return checkBoxMenuItemIcon;
	}

	public static Icon getExpandedTreeIcon() {
		if (expandedTreeIcon == null) {
			expandedTreeIcon = new ExpandedTreeIcon();
		}
		return expandedTreeIcon;
	}

	public static Icon getCollapsedTreeIcon() {
		if (collapsedTreeIcon == null) {
			collapsedTreeIcon = new CollapsedTreeIcon();
		}
		return collapsedTreeIcon;
	}

	private static class CheckBoxMenuItemIcon implements Icon, UIResource,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final int SIZE = 13;

		public int getIconWidth() {
			return SIZE;
		}

		public int getIconHeight() {
			return SIZE;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			JMenuItem b = (JMenuItem) c;
			if (b.isSelected()) {
				drawCheck(g, x, y + 1);
			}
		}
	}

	private static void drawCheck(Graphics g, int x, int y) {
		g.translate(x, y);
		g.drawLine(3, 5, 3, 5);
		g.fillRect(3, 6, 2, 2);
		g.drawLine(4, 8, 9, 3);
		g.drawLine(5, 8, 9, 4);
		g.drawLine(5, 9, 9, 5);
		g.translate(-x, -y);
	}

	private static class ExpandedTreeIcon implements Icon, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected static final int SIZE = 9;
		protected static final int HALF_SIZE = 4;

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color backgroundColor = c.getBackground();

			g.setColor(backgroundColor != null ? backgroundColor : Color.white);
			g.fillRect(x, y, SIZE - 1, SIZE - 1);
			g.setColor(Color.gray);
			g.drawRect(x, y, SIZE - 1, SIZE - 1);
			g.setColor(Color.black);
			g.drawLine(x + 2, y + HALF_SIZE, x + (SIZE - 3), y + HALF_SIZE);
		}

		public int getIconWidth() {
			return SIZE;
		}

		public int getIconHeight() {
			return SIZE;
		}
	}

	private static class CollapsedTreeIcon extends ExpandedTreeIcon {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x, y);
			g.drawLine(x + HALF_SIZE, y + 2, x + HALF_SIZE, y + (SIZE - 3));
		}
	}

	public static void antialias(Graphics g) {
		GraphicsUtils.setAntialias(g, true);
	}

	public static Rectangle getVisibleBoundsOnScreen(JComponent component) {
		Rectangle visibleRect = component.getVisibleRect();
		Point onScreen = visibleRect.getLocation();
		SwingUtilities.convertPointToScreen(onScreen, component);
		visibleRect.setLocation(onScreen);
		return visibleRect;
	}

	public static Point getPointToCenter(Component component,
			Dimension dimension) {

		Dimension screenSize = getDefaultDeviceScreenSize();

		if (component == null) {

			if (dimension.height > screenSize.height) {
				dimension.height = screenSize.height;
			}

			if (dimension.width > screenSize.width) {
				dimension.width = screenSize.width;
			}

			return new Point((screenSize.width - dimension.width) / 2,
					(screenSize.height - dimension.height) / 2);
		}

		Dimension frameDim = component.getSize();
		Rectangle dRec = new Rectangle(component.getX(), component.getY(),
				(int) frameDim.getWidth(), (int) frameDim.getHeight());

		int dialogX = dRec.x + ((dRec.width - dimension.width) / 2);
		int dialogY = dRec.y + ((dRec.height - dimension.height) / 2);

		if (dialogX <= 0 || dialogY <= 0) {

			if (dimension.height > screenSize.height) {
				dimension.height = screenSize.height;
			}

			if (dimension.width > screenSize.width) {
				dimension.width = screenSize.width;
			}

			dialogX = (screenSize.width - dimension.width) / 2;
			dialogY = (screenSize.height - dimension.height) / 2;
		}

		return new Point(dialogX, dialogY);
	}

	public static Dimension getDefaultDeviceScreenSize() {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getScreenDevices()[0];
		Dimension screenSize = gs.getDefaultConfiguration().getBounds()
				.getSize();

		return screenSize;
	}

	public static Vector<String> getSystemFonts() {
		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Font[] tempFonts = gEnv.getAllFonts();

		char dot = '.';
		int dotIndex = 0;

		char[] fontNameChars = null;
		String fontName = null;
		Vector<String> fontNames = new Vector<String>();

		for (int i = 0; i < tempFonts.length; i++) {

			fontName = tempFonts[i].getFontName();
			dotIndex = fontName.indexOf(dot);

			if (dotIndex == -1) {
				fontNames.add(fontName);
			} else {
				fontNameChars = fontName.substring(0, dotIndex).toCharArray();
				fontNameChars[0] = Character.toUpperCase(fontNameChars[0]);

				fontName = new String(fontNameChars);

				if (!fontNames.contains(fontName)) {
					fontNames.add(fontName);
				}

			}

		}

		Collections.sort(fontNames);
		return fontNames;
	}

	public static void requestFocusInWindow(final Component c) {
		invokeAndWait(new Runnable() {
			public void run() {
				c.requestFocusInWindow();
			}
		});
	}

	private static void setCursor(Cursor cursor, Component component) {
		if (component != null) {
			component.setCursor(cursor);
		}
	}

	public static void showNormalCursor(Component component) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR), component);
	}

	public static void invokeLater(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(runnable);
		} else {
			runnable.run();
		}
	}

	public static void invokeAndWait(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		} else {
			runnable.run();
		}
	}

	public static void showWaitCursor(Component component) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR), component);
	}

	private static Object displayDialog(final Component parent,
			final int optionType, final int messageType,
			final boolean wantsInput, final String icon, final String title,
			final Object message, final Object... args) {

		dialogReturnValue = null;

		Runnable runnable = new Runnable() {
			public void run() {
				showNormalCursor(parent);
				JOptionPane pane = new JOptionPane(message, messageType,
						optionType, UIManager.getIcon(icon), args);
				pane.setWantsInput(wantsInput);
				pane.setFont(UIRes.getFont());

				JDialog dialog = pane.createDialog(parent, title);

				dialog.setFont(UIRes.getFont());
				dialog.setIconImage(UIRes.icon);
				if (message instanceof IDialog) {
					((IDialog) message).setDialog(dialog);
				}

				dialog.setLocation(getPointToCenter(parent, dialog.getSize()));
				dialog.setVisible(true);
				dialog.dispose();

				if (wantsInput) {
					dialogReturnValue = pane.getInputValue();
				} else {
					dialogReturnValue = pane.getValue();
				}

			}
		};
		invokeAndWait(runnable);

		return dialogReturnValue;
	}

	public static final int displayConfirmCancelErrorMessage(Component parent,
			Object message) {
		return formatDialogReturnValue(displayDialog(parent,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, false,
				"OptionPane.errorIcon", UIMessage.error, message));
	}

	public static void showMessage(Component parent, String message,
			String title) {
		displayDialog(parent, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, false,
				"OptionPane.informationIcon", title, message);
	}

	public static void showInfoMessage(Component parent, String title,
			String message) {
		displayDialog(parent, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, false,
				"OptionPane.informationIcon", title, message);
	}

	public static void showWarningMessage(Component parent, String title,
			String message) {
		displayDialog(parent, JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE, false, "OptionPane.warningIcon",
				title, message);
	}

	public static void showErrorMessage(Component parent, String title,
			String message) {
		displayDialog(parent, JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, false, "OptionPane.errorIcon",
				title, message);
	}

	public static int showConfirmMessage(Component parent, String title,
			String message, String a, String b) {
		Object[] args = new Object[] { a, b };
		return formatDialogReturnValue(
				displayDialog(parent, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, false,
						"OptionPane.questionIcon", title, message, args), args);
	}

	public static int showConfirmMessage(Component parent, String title,
			String message, Object[] args) {
		return formatDialogReturnValue(
				displayDialog(parent, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, false,
						"OptionPane.questionIcon", title, message, args), args);
	}

	public static String showInputMessage(Component parent, String title,
			Object message) {
		return displayDialog(parent, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, true, "OptionPane.questionIcon",
				title, message).toString();
	}

	private static int formatDialogReturnValue(Object returnValue,
			Object... objs) {
		if (objs == null) {
			if (returnValue instanceof Integer) {
				return ((Integer) returnValue).intValue();
			}
		} else {
			for (int i = 0; i < objs.length; i++) {
				if (objs[i].equals(returnValue)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void scheduleGC() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
	}

	public static Color getSlightlyBrighter(Color color, float factor) {
		float[] hsbValues = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
				hsbValues);
		float hue = hsbValues[0];
		float saturation = hsbValues[1];
		float brightness = hsbValues[2];
		float newBrightness = Math.min(brightness * factor, 1.0f);
		return Color.getHSBColor(hue, saturation, newBrightness);
	}

	public static ImageIcon loadIcon(String name) {
		ImageIcon icon = null;
		if (icons.containsKey(name)) {
			icon = icons.get(name);
		} else {
			icons.put(name, icon = getImage(name));
		}
		return icon;
	}

}
