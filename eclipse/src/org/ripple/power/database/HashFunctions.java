package org.ripple.power.database;

import org.ripple.power.utils.MathUtils;

public class HashFunctions {
	private int firstBytes;
	private int tableSize;

	public HashFunctions(int tableSize, int minPwLength, int maxPwLength) {
		this.firstBytes = minPwLength + (maxPwLength - minPwLength) / 2;
		this.tableSize = tableSize;
	}

	public int hash(HashBytes b) {
		int hash = 0;
		byte[] bytes = b.getBytes();
		int size = bytes.length;
		for (int i = 1; i < firstBytes; i++) {
			if (i < size) {
				hash += MathUtils.pow(bytes[i - 1], i);
			}
		}
		return hash % tableSize;
	}
}
