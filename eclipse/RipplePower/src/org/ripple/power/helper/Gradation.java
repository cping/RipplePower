package org.ripple.power.helper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.GraphicsUtils;

public class Gradation {

	private Color start;

	private Color end;

	private int width, height, alpha;

	private BufferedImage drawWidth, drawHeight;

	private static HashMap<String, Gradation> lazyGradation;

	public static Gradation getInstance(Color s, Color e, int w, int h) {
		return getInstance(s, e, w, h, 125);
	}

	public static Gradation getInstance(Color s, Color e, int w, int h, int alpha) {
		if (lazyGradation == null) {
			lazyGradation = new HashMap<String, Gradation>(10);
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, s.getRGB());
		hashCode = LSystem.unite(hashCode, e.getRGB());
		hashCode = LSystem.unite(hashCode, w);
		hashCode = LSystem.unite(hashCode, h);
		hashCode = LSystem.unite(hashCode, alpha);
		String key = String.valueOf(hashCode);
		Gradation o = (Gradation) lazyGradation.get(key);
		if (o == null) {
			lazyGradation.put(key, o = new Gradation(s, e, w, h, alpha));
		}
		return o;
	}

	private Gradation() {

	}

	private Gradation(Color s, Color e, int w, int h, int alpha) {
		this.start = s;
		this.end = e;
		this.width = w;
		this.height = h;
		this.alpha = alpha;
	}

	public synchronized void drawWidth(Graphics g, int x, int y) {
		try {
			if (drawWidth == null) {
				drawWidth = GraphicsUtils.createImage(width, height, true);
				Graphics gl = drawWidth.getGraphics();
				for (int i = 0; i < width; i++) {
					gl.setColor(new Color((start.getRed() * (width - i)) / width + (end.getRed() * i) / width,
							(start.getGreen() * (width - i)) / width + (end.getGreen() * i) / width,
							(start.getBlue() * (width - i)) / width + (end.getBlue() * i) / width, alpha));
					gl.drawLine(i, 0, i, height);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawWidth, x, y, null);
		} catch (Exception e) {
			for (int i = 0; i < width; i++) {
				g.setColor(new Color((start.getRed() * (width - i)) / width + (end.getRed() * i) / width,
						(start.getGreen() * (width - i)) / width + (end.getGreen() * i) / width,
						(start.getBlue() * (width - i)) / width + (end.getBlue() * i) / width, alpha));
				g.drawLine(i + x, y, i + x, y + height);
			}
		}
	}

	public synchronized void drawHeight(Graphics g, int x, int y) {
		try {
			if (drawHeight == null) {
				drawHeight = GraphicsUtils.createImage(width, height, true);
				Graphics gl = drawHeight.getGraphics();
				for (int i = 0; i < height; i++) {
					gl.setColor(new Color((start.getRed() * (height - i)) / height + (end.getRed() * i) / height,
							(start.getGreen() * (height - i)) / height + (end.getGreen() * i) / height,
							(start.getBlue() * (height - i)) / height + (end.getBlue() * i) / height, alpha));
					gl.drawLine(0, i, width, i);
				}
				gl.dispose();
				gl = null;
			}
			g.drawImage(drawHeight, x, y, null);
		} catch (Exception e) {
			for (int i = 0; i < height; i++) {
				g.setColor(new Color((start.getRed() * (height - i)) / height + (end.getRed() * i) / height,
						(start.getGreen() * (height - i)) / height + (end.getGreen() * i) / height,
						(start.getBlue() * (height - i)) / height + (end.getBlue() * i) / height, alpha));
				g.drawLine(x, i + y, x + width, i + y);
			}
		}
	}

	public static void close() {
		if (lazyGradation == null) {
			return;
		}
		Set<?> entrys = lazyGradation.entrySet();
		for (Iterator<?> it = entrys.iterator(); it.hasNext();) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			Gradation g = (Gradation) e.getValue();
			if (g != null) {
				g.dispose();
				g = null;
			}
		}
	}

	public void dispose() {
		if (drawWidth != null) {
			drawWidth.flush();
		}
		if (drawHeight != null) {
			drawHeight.flush();
		}
	}

}
