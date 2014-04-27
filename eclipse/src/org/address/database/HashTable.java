package org.address.database;

import java.util.Iterator;

import org.address.Closed;

public class HashTable implements Iterable<HashBytes>, Closed {

	private HashBytes[] bytesTable;
	private HashFunctions hf;
	private int size;
	private boolean pClose;

	public HashTable(int size, int minPwLength, int maxPwLength) {
		int prime = calculate(size);
		this.bytesTable = new HashBytes[prime];
		this.hf = new HashFunctions(prime, minPwLength, maxPwLength);
		this.pClose = false;
	}

	private int calculate(int size) {
		int i = size / 2;
		while (true) {
			boolean isPrime = true;
			for (int j = 2; j < i; j++) {
				if (i % j == 0) {
					isPrime = false;
					break;
				}
			}
			if (isPrime) {
				return i;
			}
			i++;
		}
	}

	public void insert(HashBytes key) {
		if(pClose){
			return;
		}
		int index = hf.hash(key);
		if (bytesTable[index] == null) {
			bytesTable[index] = key;
		} else {
			HashBytes currentBytes = bytesTable[index];
			if (currentBytes.equals(key)) {
				return;
			}
			while (currentBytes.next != null) {
				if (currentBytes.equals(key)) {
					return;
				}
				currentBytes = currentBytes.next;
			}
			currentBytes.next = key;
		}
		size++;
	}

	public void insert(HashBytes key, HashBytes value) {
		if(pClose){
			return;
		}
		key.value = value;
		insert(key);
	}

	public boolean contains(HashBytes key) {
		if(pClose){
			return false;
		}
		HashBytes found = findKey(key);
		if (found == null) {
			return false;
		}
		return true;
	}

	public HashBytes search(HashBytes key) {
		if(pClose){
			return null;
		}
		HashBytes found = findKey(key);
		if (found == null) {
			return null;
		}
		return found.value;
	}

	public int size() {
		if(pClose){
			return 0;
		}
		return size;
	}

	public String toString() {
		if(pClose){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int i = 0; i < bytesTable.length; i++) {
			if (i + 1 == bytesTable.length) {
				sb.append(bytesTable[i]);
			} else {
				sb.append(bytesTable[i]);
				sb.append(", ");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}

	public HashBytes[] getBytes() {
		if(pClose){
			return null;
		}
		return bytesTable;
	}

	@Override
	public Iterator<HashBytes> iterator() {
		if(pClose){
			return null;
		}
		return new HashIterator(bytesTable, size);
	}

	private HashBytes findKey(HashBytes key) {
		if(pClose){
			return null;
		}
		int index = hf.hash(key);
		HashBytes otherBytes = bytesTable[index];
		while (otherBytes != null && !key.equals(otherBytes)) {
			if (otherBytes.next != null) {
				otherBytes = otherBytes.next;
			} else {
				break;
			}
		}
		return otherBytes;
	}

	@Override
	public void close() {
		if (bytesTable != null) {
			bytesTable = null;
			pClose = true;
		}

	}
}
