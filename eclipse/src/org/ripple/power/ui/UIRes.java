package org.ripple.power.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.Updateable;

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
			_font = new Font(LangConfig.fontName, 0, 14);
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

	public static InputStream getStream(String path) {
		path = computePath(path);
		InputStream is = classLoader.getResourceAsStream(path);
		if (is == null) {
			throw new RuntimeException("File not found: " + path);
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
}
