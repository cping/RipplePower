package org.ripple.power.ui;

import java.util.HashMap;

import javax.swing.ImageIcon;

import org.ripple.power.ui.graphics.LImage;

public class OtherImages {

	private static HashMap<String, ImageIcon> _speed_images = new HashMap<String, ImageIcon>(
			10);
	private static LImage _speed_image;

	public synchronized static ImageIcon getSpeedImage(String name) {
		name = name.toLowerCase();
		ImageIcon image = _speed_images.get(name);
		if (image == null) {
			if (_speed_image == null) {
				_speed_image = LImage.createImage("icons/speed.png");
			}
			if ("empty".equals(name)) {
				image = new ImageIcon(_speed_image
						.getSubImageSize(0, 0, 34, 20).getBufferedImage());
			} else if ("lv1".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 0, 68,
						20).getBufferedImage());
			} else if ("lv2".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 0, 102,
						20).getBufferedImage());
			} else if ("lv3".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(0, 20, 34,
						40).getBufferedImage());
			} else if ("lv4".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 20, 68,
						40).getBufferedImage());
			} else if ("lv5".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 20, 102,
						40).getBufferedImage());
			} else if ("black".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(0, 40, 34,
						60).getBufferedImage());
			} else if ("lv0".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 40, 68,
						60).getBufferedImage());
			} else if ("all".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 40, 102,
						60).getBufferedImage());
			}
			_speed_images.put(name, image);
		}
		return image;
	}

}
