package org.ripple.power.database.secrecy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecureTable<T extends TableEntry> implements Table<T> {

	private static final long serialVersionUID = 1L;
	private final Map<Long, T> tableEntryMap;
	private final String name;

	public SecureTable(String name) {
		this.tableEntryMap = new ConcurrentHashMap<>();
		this.name = name;
	}

	@Override
	public T getEntry(Long id) {
		if (this.tableEntryMap.containsKey(id)) {
			return this.tableEntryMap.get(id);
		}
		return null;
	}

	@Override
	public synchronized void putEntry(T entry) {
		Long id = entry.getId();
		if (id == null || id == 0L) {
			id = this.tableEntryMap.size() + 1L;
			entry.setId(id);
		}
		this.tableEntryMap.put(id, entry);
	}

	@Override
	public List<T> getAll() {
		return new ArrayList<>(this.tableEntryMap.values());
	}

	@Override
	public String getName() {
		return this.name;
	}

}