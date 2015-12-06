package org.ripple.power.qr;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

public class EncoderDecoder {

	public static BufferedImage getEncoder(String address, int w, int h) {
		EncoderDecoder encoder = new EncoderDecoder(w, h);
		return encoder.encode(address);
	}

	public static String getDecoder(BufferedImage image, int w, int h)
			throws IOException {
		EncoderDecoder decoder = new EncoderDecoder(w, h);
		return decoder.decode(image);
	}

	private int width;
	private int height;

	public EncoderDecoder(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public BufferedImage encode(String data) {
		BitMatrix matrix;
		com.google.zxing.Writer writer = new QRCodeWriter();
		try {
			matrix = writer.encode(data,
					com.google.zxing.BarcodeFormat.QR_CODE, width, height);
		} catch (com.google.zxing.WriterException e) {
			return null;
		}
		int width = matrix.getWidth();
		int height = matrix.getHeight();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean imageValue = matrix.get(x, y);
				image.setRGB(x, y, (imageValue ? 0 : 0xFFFFFF));
			}
		}
		return image;
	}

	public String decode(BufferedImage image) throws IOException {

		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		QRCodeReader reader = new QRCodeReader();

		try {

			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
			Result result = reader.decode(bitmap, hints);

			return result.getText();
		} catch (ReaderException e) {
			throw new IOException(e);
		}

	}
}
