package org.ripple.power.database.secrecy;

import java.io.Serializable;
import java.util.List;

public interface Table<T extends TableEntry> extends Serializable {

	public T getEntry(Long id);

	public void putEntry(T entry);

	public List<T> getAll();

	public String getName();

}
