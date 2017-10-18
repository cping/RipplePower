package org.ripple.power.helper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import org.ripple.power.utils.GraphicsUtils;

public class ImageSet {

	private HashMap<String, Image> ImageHash;
	private HashMap<String, BufferedImage> BufferedImageHash;

	public ImageSet() {
		ImageHash = new HashMap<String, Image>();
		BufferedImageHash = new HashMap<String, BufferedImage>();
	}

	public void loadWait(Image img) {
		Image array[] = new Image[1];
		array[0] = img;
		GraphicsUtils.waitImage(img);
	}

	public void SplitWindow(BufferedImage img) {
		int FrameSize = 6;
		int CornerSize = 14;
		int WholeSize = 64;
		int BorderLength = 8;
		BufferedImage SPLITS[] = new BufferedImage[8];
		SPLITS[0] = img.getSubimage(WholeSize / 2 - BorderLength / 2, 0, BorderLength, FrameSize);
		SPLITS[1] = img.getSubimage(0, WholeSize / 2 - BorderLength / 2, FrameSize, BorderLength);
		SPLITS[2] = img.getSubimage(WholeSize / 2 - BorderLength / 2, WholeSize - FrameSize, BorderLength, FrameSize);
		SPLITS[3] = img.getSubimage(WholeSize - FrameSize, WholeSize / 2 - BorderLength / 2, FrameSize, BorderLength);
		SPLITS[4] = img.getSubimage(0, 0, CornerSize, CornerSize);
		SPLITS[5] = img.getSubimage(0, WholeSize - CornerSize, CornerSize, CornerSize);
		SPLITS[6] = img.getSubimage(WholeSize - CornerSize, WholeSize - CornerSize, CornerSize, CornerSize);
		SPLITS[7] = img.getSubimage(WholeSize - CornerSize, 0, CornerSize, CornerSize);
		for (int i = 0; i < SPLITS.length; i++) {
			BufferedImageHash.put((new StringBuilder("win")).append(i).toString(), SPLITS[i]);
		}

	}

	public BufferedImage getBufferdImage(String str) {
		return (BufferedImage) BufferedImageHash.get(str);
	}

	public Image getImage(String str) {
		return (Image) ImageHash.get(str);
	}

}
