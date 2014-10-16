package org.ripple.power.ui;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.ripple.power.helper.Paramaters;

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
