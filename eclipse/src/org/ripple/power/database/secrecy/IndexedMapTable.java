package org.ripple.power.database.secrecy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexedMapTable<T extends IndexedTableEntry> implements Table<T> {

	private static final long serialVersionUID = 1L;
	private final Map<String, Long> tableEntryIndexMap;
	private final SecureTable<T> backingMapTable;

	public IndexedMapTable(SecureTable<T> backingMapTable) {
		this.backingMapTable = backingMapTable;
		this.tableEntryIndexMap = new ConcurrentHashMap<>();
	}

	public T getEntry(String strId) {
		if (this.tableEntryIndexMap.containsKey(strId)) {
			Long id = this.tableEntryIndexMap.get(strId);
			if (id != null) {
				return this.backingMapTable.getEntry(id);
			}
		}
		return null;
	}

	@Override
	public synchronized void putEntry(T entry) {
		String strId = entry.getIndexId().toLowerCase();
		if (strId == null) {
			throw new NullPointerException("id not null");
		}
		this.backingMapTable.putEntry(entry);
		this.tableEntryIndexMap.put(entry.getIndexId(), entry.getId());
	}

	@Override
	public T getEntry(Long id) {
		return this.backingMapTable.getEntry(id);
	}

	@Override
	public List<T> getAll() {
		return this.backingMapTable.getAll();
	}

	@Override
	public String getName() {
		return this.backingMapTable.getName();
	}

}
