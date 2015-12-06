package org.ripple.power.database.secrecy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serialization {

	@SuppressWarnings("unchecked")
	public static <T> T inflate(byte[] serializedObject) throws IOException,
			ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(serializedObject))) {
			return (T) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			throw ex;
		}
	}

	public static <T extends Serializable> byte[] deflate(T serializableObject)
			throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);) {
			oos.writeObject(serializableObject);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException ex) {
			throw ex;
		}
	}
}
