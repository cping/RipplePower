package org.ripple.power.wallet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.address.utils.CoinUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class WalletSeed {

	private String pPassword;

	private final OpenSSL pSsl;

	public WalletSeed(String pass) {
		this.pPassword = pass;
		this.pSsl = new OpenSSL();
	}

	public String getPassword() {
		return pPassword;
	}

	public void setPassword(String password) {
		this.pPassword = password;
	}

	public byte[] decrypt(byte[] bytes) throws IOException {
		byte[] buffer = bytes;
		if (pPassword != null && pPassword.length() > 0) {
			buffer = pSsl.decrypt(bytes, pPassword);
		} else {
			buffer = bytes;
		}
		ByteArrayInputStream ins = new ByteArrayInputStream(buffer);
		DataInputStream db = new DataInputStream(ins);
		byte[] appName = LSystem.applicationName.getBytes(LSystem.encoding);
		int header = db.read();
		byte[] valueBuffer = new byte[header];
		db.read(valueBuffer, 0, header);
		if (!Arrays.equals(valueBuffer, appName)) {
			throw new IOException("The software does not recognize the name !");
		}
		int length = db.read();
		valueBuffer = new byte[length];
		db.read(valueBuffer, 0, length);
		int size = valueBuffer.length;
		for (int i = 0; i < size; i++) {
			valueBuffer[i] ^= 0xF9;
		}
		return valueBuffer;
	}

	public byte[] encrypt(byte[] bytes) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		DataOutputStream db = new DataOutputStream(out);
		byte[] appName = LSystem.applicationName.getBytes(LSystem.encoding);
		int applength = appName.length;
		db.write(applength);
		db.write(appName);
		int size = bytes.length;
		for (int i = 0; i < size; i++) {
			bytes[i] ^= 0xF9;
		}
		db.write(bytes.length);
		db.write(bytes);
		byte[] buffer = out.toByteArray();
		out.close();
		if (pPassword != null && pPassword.length() > 0) {
			buffer = pSsl.encrypt(buffer, pPassword);
		}
		return buffer;
	}

	public void save(File file, String context) throws Exception {
		if (context != null && context.length() > 0) {
			FileUtils.makedirs(file);
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					file, false));
			String pass = LSystem.applicationPassword.trim();
			byte[] buffer = context.getBytes(LSystem.encoding);
			byte[] keyChars = pass.getBytes(LSystem.encoding);
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] ^= keyChars[i % keyChars.length];
			}
			String text = CoinUtils.toHex(buffer);
			buffer = text.getBytes(LSystem.encoding);
			int size = buffer.length;
			for (int i = 0; i < size; i++) {
				buffer[i] ^= 0xA1;
			}
			buffer = WalletCryptos.encrypt(pass, buffer);
			out.write(buffer);
			out.close();
		}
	}

	public String load(File file) throws Exception {
		if (!file.exists()) {
			return null;
		}
		String pass = LSystem.applicationPassword.trim();
		byte[] buffer = FileUtils.readBytesFromFile(file);
		buffer = WalletCryptos.decrypt(pass, buffer);
		int size = buffer.length;
		for (int i = 0; i < size; i++) {
			buffer[i] ^= 0xA1;
		}
		String text = new String(buffer, LSystem.encoding);
		buffer = CoinUtils.fromHex(text);
		byte[] keyChars = pass.getBytes(LSystem.encoding);
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] ^= keyChars[i % keyChars.length];
		}
		return new String(buffer, LSystem.encoding);
	}
}
