package org.ripple.power.database.secrecy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.ripple.power.utils.ByteUtils;
import org.ripple.power.wallet.Passphrase;

public final class SecureDataBase {

	private final Passphrase passphrase;
	private final File dbPath;
	private static final String SECURE_INDEX = "SECURE_INDEX";

	public SecureDataBase(File file) {
		this.dbPath = file;
		this.passphrase = new Passphrase();
		this.ensureFile();
	}

	public SecureDataBase(String path) {
		this.dbPath = new File(path);
		this.passphrase = new Passphrase();
		this.ensureFile();
	}

	private void ensureFile() {
		File dbFile = this.dbPath;
		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void clear() {
		File dbFile = this.dbFile();
		if (dbFile.exists()) {
			dbFile.delete();
		}
		ensureFile();
	}

	public void lock() {
		this.passphrase.clear();
	}

	public boolean isLocked() {
		return this.passphrase.isClear();
	}

	public void setPassphrase(String passphrase) throws Exception {
		this.passphrase.setPassphrase(passphrase);
		if (dbFile().length() == 0) {
			return;
		}
		try {
			this.getMasterIndex();
		} catch (Exception ex) {
			lock();
			ex.printStackTrace();
		}
	}

	private File dbFile() {
		ensureFile();
		return this.dbPath;
	}

	private ZipOutputStream outZip() throws FileNotFoundException {
		return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dbFile())));
	}

	private ZipFile zipDbFile() throws IOException {
		return new ZipFile(dbFile());
	}

	public boolean isEmpty() {
		return (dbFile().length() < 1L);
	}

	public SecureIndex getMasterIndex() throws Exception {
		try (ZipFile zipFile = zipDbFile();
				InputStream masterIndexInStream = zipFile.getInputStream(zipFile.getEntry(SECURE_INDEX));) {
			byte[] encryptedMasterIndex = ByteUtils.readFully(masterIndexInStream);
			byte[] decryptedMasterIndex = Passphrase.decrypt(this.passphrase, encryptedMasterIndex);
			return Serialization.<SecureIndex>inflate(decryptedMasterIndex);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void commitMasterIndex(SecureIndex masterIndex) throws Exception {
		try (ZipOutputStream zipOut = outZip();) {
			for (BaseTable fileTable : masterIndex.getFileTables()) {
				for (BaseTableEntry fileTableEntry : fileTable.getAll()) {
					if (!fileTableEntry.isSourceAttached()) {
						continue;
					}
					zipOut.putNextEntry(new ZipEntry(fileTableEntry.getFileNameHash()));
					Path sourceFilePath = fileTableEntry.getSourceFilePath();
					byte[] fileBytes = ByteUtils.readFully(sourceFilePath);
					zipOut.write(Passphrase.encrypt(this.passphrase, fileBytes));
					zipOut.flush();
					zipOut.closeEntry();
					fileTableEntry.detachSource();
				}
			}
			masterIndex.incrementCommitCount();
			byte[] encryptedMasterIndex = Passphrase.encrypt(this.passphrase,
					Serialization.<SecureIndex>deflate(masterIndex));
			zipOut.putNextEntry(new ZipEntry(SECURE_INDEX));
			zipOut.write(encryptedMasterIndex);
			zipOut.flush();
			zipOut.closeEntry();
			zipOut.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public byte[] loadFile(BaseTableEntry fileTableEntry) throws Exception {
		if (fileTableEntry == null) {
			return null;
		}
		byte[] fileBytes = null;
		ensureFile();
		try {
			if (Files.size(dbFile().toPath()) < 1L) {
				return fileBytes;
			}
		} catch (IOException ex) {
			throw ex;
		}
		byte[] encryptedFile;
		try (ZipFile zipFile = zipDbFile();
				InputStream fileInStream = zipFile
						.getInputStream(zipFile.getEntry(fileTableEntry.getFileNameHash()));) {
			encryptedFile = ByteUtils.readFully(fileInStream);
		}
		fileBytes = Passphrase.decrypt(this.passphrase, encryptedFile);
		return fileBytes;
	}

}
