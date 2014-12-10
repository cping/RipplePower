package org.ripple.power.database.secrecy;

import java.nio.file.Path;
import java.util.UUID;

public class BaseTableEntry implements IndexedTableEntry {
	
	private static final long serialVersionUID = 1L;

	private long id;
	private final String fileNameHash;
	private Path sourceFilePath;
	private String fileName;
	private long sizeInBytes;
	private String message;

	public BaseTableEntry(Path sourceFilePath, String message) {
		this.fileNameHash = UUID.randomUUID().toString();
		this.sourceFilePath = sourceFilePath;
		this.fileName = sourceFilePath.getFileName().toString();
		this.sizeInBytes = sourceFilePath.toFile().length();
		this.message = message;
	}

	public String getFileName() {
		return this.fileName;
	}
	
	public String getFileNameHash() {
		return this.fileNameHash;
	}

	public Path getSourceFilePath() {
		return this.sourceFilePath;
	}

	public long getSizeInBytes() {
		return this.sizeInBytes;
	}

	public boolean isSourceAttached() {
		return !(this.sourceFilePath == null);
	}

	public void detachSource() {
		this.sourceFilePath = null;
	}

	@Override
	public String getIndexId() {
		StringBuilder sb = new StringBuilder(this.fileName);
		return sb.toString().toLowerCase();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

}
