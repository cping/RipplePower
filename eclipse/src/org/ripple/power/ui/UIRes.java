package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.ripple.power.collection.ArrayByte;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.GraphicsUtils;
import org.spongycastle.util.encoders.Hex;

public class UIRes {

	public static String PATH = "res/";
	private static Map<String, ImageIcon> imageIcons = new HashMap<String, ImageIcon>();

	private static ClassLoader classLoader;

	static {
		try {
			classLoader = UIRes.class.getClassLoader();
		} catch (Exception e) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
	}

	private static Font _font;

	public static Font getFont() {
		if (_font == null) {
			_font = GraphicsUtils.getFont(14);
		}
		return _font;
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
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
        titled.setTitleFont(new Font("Verdana", 0, 13));
        titled.setTitleColor(new Color(213, 225, 185));
        Border empty = new EmptyBorder(5, 8, 5, 8);
        CompoundBorder border = new CompoundBorder(titled, empty);
        textField.setBorder(border);
        textField.setForeground(new Color(143, 170, 220));
        textField.setFont(new Font("Monospaced", 0, 13));
    }

    public static void addStyle(JTextArea textArea, String labelName, boolean isBorder) {
        Border border = null;
        if (isBorder) {
            Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
            TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
            titled.setTitleFont(new Font("Verdana", 0, 13));
            titled.setTitleColor(new Color(213, 225, 185));
        }
        textArea.setBorder(border);
        textArea.setForeground(new Color(143, 170, 220));
        textArea.setFont(new Font("Monospaced", 0, 13));
    }

    public static void addStyle(JScrollPane jScrollPane, String labelName) {
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titled = BorderFactory.createTitledBorder(line, labelName);
        titled.setTitleFont(new Font("Verdana", 0, 13));
        titled.setTitleColor(new Color(213, 225, 185));
        Border empty = new EmptyBorder(5, 8, 5, 8);
        CompoundBorder border = new CompoundBorder(titled, empty);
        jScrollPane.setBorder(border);
        jScrollPane.setForeground(new Color(143, 170, 220));
        jScrollPane.setBackground(Color.WHITE);
        jScrollPane.setFont(new Font("Monospaced", 0, 13));
        jScrollPane.setHorizontalScrollBar(null);
    }

    public static void addStyle(JTable jTable) {
        jTable.setForeground(new Color(143, 170, 220));
        jTable.setBackground(Color.WHITE);
        jTable.setFont(new Font("Monospaced", 0, 13));
    }

    public static String getHexStyledText(byte[] data) {
        String[] dataHex = Hex.toHexString(data).split("(?<=\\G.{2})");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < dataHex.length; ++i) {
            sb.append(dataHex[i]).append(" ");
            if ((i + 1) % 8 == 0 && i != 0) sb.append("\n");
        }
        return sb.toString();
    }
}
