package org.ripple.power.ui.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.GraphicsUtils;

public class SerializablelImage implements Serializable {

	private static final long serialVersionUID = -1982984646473630901L;

	private transient BufferedImage image = null;

	public SerializablelImage() {
		this.image = null;
	}

	public SerializablelImage(Image img) {
		this.image = GraphicsUtils.getBufferImage(img);
	}

	public SerializablelImage(BufferedImage img) {
		this.image = img;
	}

	public void setImage(BufferedImage img) {
		this.image = img;
	}

	public BufferedImage getImage() {
		return image;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		if (image == null) {
			LSystem.writeInt(out, 0);
		} else {
			LSystem.writeInt(out, 1);
			WritableRaster wr = image.getRaster();
			int pixels[] = (int[]) wr.getPixels(0, 0, image.getWidth(), image
					.getHeight(), (int[]) null);
			LSystem.writeInt(out, image.getWidth());
			LSystem.writeInt(out, image.getHeight());
			LSystem.writeInt(out, pixels.length);
			for (int i = 0; i < pixels.length; i++) {
				LSystem.writeInt(out, pixels[i]);
			}
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		int result = LSystem.readInt(in);
		if (result == 1) {
			int width = LSystem.readInt(in);
			int height = LSystem.readInt(in);
			int pixelCount = LSystem.readInt(in);
			int pixels[] = new int[pixelCount];
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = LSystem.readInt(in);
			}
			BufferedImage image = GraphicsUtils
					.createImage(width, height, true);
			WritableRaster wr = image.getRaster();
			wr.setPixels(0, 0, width, height, pixels);
			this.image = image;
		}
	}

}