package org.ripple.power.speed;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ripple.power.config.LSystem;

public class SpeedTest {

	public static InputStream getSyncUrlRangeInputStream(URL url,
			ByteRange byteRange, int index,
			SpeedListener networkSpeedListener) throws IOException {
	
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();
		String rangeProperty = byteRange.getRangeProperty();
		connection.addRequestProperty("range", rangeProperty);
		return new SpeedOMeterInpotStream(connection.getInputStream(),
				byteRange.getRangeSize(), index, networkSpeedListener);
	}

	public static InputStream getAsyncUrlRangeInputStream(URL url,
			ByteRange byteRange, int index,
			SpeedListener networkSpeedListener) throws IOException {
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();
		String rangeProperty = byteRange.getRangeProperty();
		connection.addRequestProperty("range", rangeProperty);
		AsyncInputStream ais = new AsyncInputStream(
				connection.getInputStream(), byteRange.getRangeSize(), index,
				networkSpeedListener);
		ais.start();
		return ais;
	}

	public static InputStream getAsyncUrlRangeInputStream(URL url,
			ByteRange byteRange) throws IOException {
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();
		String rangeProperty = byteRange.getRangeProperty();
		connection.addRequestProperty("range", rangeProperty);
		return new BufferedInputStream(connection.getInputStream());
	}


}
