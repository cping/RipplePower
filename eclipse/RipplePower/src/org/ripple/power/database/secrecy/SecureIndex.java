package org.ripple.power.database.secrecy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecureIndex implements Serializable {

	private static final long serialVersionUID = 1L;

	private long commitCount;
	private final Map<String, BaseTable> fileTables;
	@SuppressWarnings("rawtypes")
	private final Map<String, IndexedMapTable> indexedMapTables;
	@SuppressWarnings("rawtypes")
	private final Map<String, SecureTable> mapTables;

	public SecureIndex() {
		this.commitCount = 0L;
		this.fileTables = new ConcurrentHashMap<>();
		this.indexedMapTables = new ConcurrentHashMap<>();
		this.mapTables = new ConcurrentHashMap<>();
	}

	public void putFileTable(BaseTable fileTable) {
		this.fileTables.put(fileTable.getName(), fileTable);
	}

	public BaseTable getFileTable(String name) {
		if (this.fileTables.containsKey(name)) {
			return this.fileTables.get(name);
		}
		return null;
	}

	public List<BaseTable> getFileTables() {
		return new ArrayList<>(this.fileTables.values());
	}

	@SuppressWarnings("rawtypes")
	public void putIndexedMapTable(IndexedMapTable indexedMapTable) {
		this.indexedMapTables.put(indexedMapTable.getName(), indexedMapTable);
	}

	@SuppressWarnings("rawtypes")
	public IndexedMapTable getIndexedMapTable(String name) {
		if (this.indexedMapTables.containsKey(name)) {
			return this.indexedMapTables.get(name);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void putMapTable(SecureTable mapTable) {
		this.mapTables.put(mapTable.getName(), mapTable);
	}

	@SuppressWarnings("rawtypes")
	public SecureTable getMapTable(String name) {
		if (this.mapTables.containsKey(name)) {
			return this.mapTables.get(name);
		}
		return null;
	}

	public void incrementCommitCount() {
		synchronized (this) {
			if (this.commitCount > Long.MAX_VALUE - 10000L) {
				this.commitCount = 0L;
			}
			this.commitCount++;
		}
	}

}
