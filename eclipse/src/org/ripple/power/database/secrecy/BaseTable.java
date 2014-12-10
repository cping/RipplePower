package org.ripple.power.database.secrecy;

import java.util.UUID;

public class BaseTable extends IndexedMapTable<BaseTableEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String crypticName;

	public BaseTable(String name) {
		super(new SecureTable<BaseTableEntry>(name));
		this.crypticName = UUID.randomUUID().toString();
	}

	public String getCrypticName() {
		return this.crypticName;
	}

}
