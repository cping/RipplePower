package org.ripple.power.database;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.config.LSystem;
import org.ripple.power.database.secrecy.BaseTable;
import org.ripple.power.database.secrecy.BaseTableEntry;
import org.ripple.power.database.secrecy.IndexedMapTable;
import org.ripple.power.database.secrecy.PasswordSecureData;
import org.ripple.power.database.secrecy.SecureData;
import org.ripple.power.database.secrecy.SecureDataBase;
import org.ripple.power.database.secrecy.SecureIndex;
import org.ripple.power.database.secrecy.SecureTable;
import org.ripple.power.utils.ByteUtils;

public class SafeData {

	private SecureDataBase secureDatabase;
	private static final String DEFAULT_FILE_NAME = "safe.data";
	private static final String NOTES = "NOTES";
	private static final String PASSWORDS = "PASSWORDS";

	private static final String TABLE = "SAFE_TABLE";

	public SafeData() {
		this(LSystem.getDirectory() + "/" + DEFAULT_FILE_NAME);
	}

	public SafeData(String dbPath) {
		this.secureDatabase = new SecureDataBase(dbPath);
	}

	private void init() throws Exception {
		if (this.secureDatabase.isEmpty()) {
			SecureIndex masterIndex = new SecureIndex();
			masterIndex.putIndexedMapTable(new IndexedMapTable<>(
					new SecureTable<SecureData>(NOTES)));
			masterIndex.putIndexedMapTable(new IndexedMapTable<>(
					new SecureTable<PasswordSecureData>(PASSWORDS)));
			masterIndex.putFileTable(new BaseTable(TABLE));
			this.secureDatabase.commitMasterIndex(masterIndex);
		}
	}

	public boolean isLocked() {
		return this.secureDatabase.isLocked();
	}

	public void setPassphrase(String string) throws Exception {
		this.secureDatabase.setPassphrase(string);
		try {
			init();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void lock() {
		this.secureDatabase.lock();
	}

	public void clear() throws Exception {
		this.secureDatabase.clear();
		this.init();
	}

	public void putNote(SecureData dataToSave) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		@SuppressWarnings("unchecked")
		IndexedMapTable<SecureData> noteTable = masterIndex
				.getIndexedMapTable(NOTES);
		noteTable.putEntry(dataToSave);
		this.secureDatabase.commitMasterIndex(masterIndex);
	}

	public SecureData getNote(String title) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		@SuppressWarnings("unchecked")
		IndexedMapTable<SecureData> noteTable = masterIndex
				.getIndexedMapTable(NOTES);
		return noteTable.getEntry(title);
	}

	public void putPasswordNote(PasswordSecureData dataToSave) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		@SuppressWarnings("unchecked")
		IndexedMapTable<PasswordSecureData> passwordNoteTable = masterIndex
				.getIndexedMapTable(PASSWORDS);
		passwordNoteTable.putEntry(dataToSave);
		this.secureDatabase.commitMasterIndex(masterIndex);
	}

	public PasswordSecureData getPasswordNote(String title) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		@SuppressWarnings("unchecked")
		IndexedMapTable<PasswordSecureData> passwordNoteTable = masterIndex
				.getIndexedMapTable(PASSWORDS);
		return passwordNoteTable.getEntry(title);
	}

	public void putFile(File selectedFile) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		BaseTable fileTable = masterIndex.getFileTable(TABLE);
		fileTable.putEntry(new BaseTableEntry(selectedFile.toPath(), ""));
		this.secureDatabase.commitMasterIndex(masterIndex);
	}

	public void getFile(String fileName) throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		BaseTable fileTable = masterIndex.getFileTable(TABLE);
		BaseTableEntry fileEntry = fileTable.getEntry(fileName);
		byte[] fileBytes = this.secureDatabase.loadFile(fileEntry);
		if (fileBytes != null) {
			ByteUtils.writeFully(Paths.get(fileName), fileBytes);
		}
	}

	public List<String[]> listFiles() throws Exception {
		SecureIndex masterIndex = this.secureDatabase.getMasterIndex();
		IndexedMapTable<BaseTableEntry> fileTable = masterIndex
				.getFileTable(TABLE);
		List<BaseTableEntry> fileEntries = fileTable.getAll();
		List<String[]> fileListing = new ArrayList<>(fileEntries.size());
		for (BaseTableEntry fileTableEntry : fileEntries) {
			StringBuilder sb = new StringBuilder(fileTableEntry.getFileName());
			sb.append(", ");
			sb.append(fileTableEntry.getFileNameHash());
			sb.append(", ");
			sb.append(fileTableEntry.getSizeInBytes());
			sb.append("bytes.");
			String[] listing = { (sb.toString()) };
			fileListing.add(listing);
		}
		return fileListing;
	}
}
