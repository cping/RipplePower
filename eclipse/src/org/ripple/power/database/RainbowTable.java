package org.ripple.power.database;

import org.ripple.power.CoinUtils;
/**
 * 一个简单的“非专用”彩虹表实现，可以实现“增查”功能，但速度显然无法和专用表媲美。
 * 
 * @author cping
 *
 */
public class RainbowTable {

	private final HashTable pTable;

	public RainbowTable(int size, int minPwLength, int maxPwLength) {
		this.pTable = new HashTable(size, minPwLength, maxPwLength);
	}

	public RainbowTable(int size) {
		this(size, 0, 64);
	}

	public RainbowTable(int size, int length) {
		this(size, 0, length);
	}

	public void addHashString(String key) {
		pTable.insert(new HashBytes(CoinUtils.fromHex(key)));
	}

	public void add(String key) {
		pTable.insert(new HashBytes(key.getBytes()));
	}

	public void add(String key, String value) {
		pTable.insert(new HashBytes(key.getBytes()),
				new HashBytes(value.getBytes()));
	}

	public void add(byte[] key) {
		pTable.insert(new HashBytes(key));
	}

	public void add(byte[] key, byte[] value) {
		pTable.insert(new HashBytes(key), new HashBytes(value));
	}

	public boolean find(String key) {
		return pTable.contains(new HashBytes(key.getBytes()));
	}

	public boolean find(byte[] key) {
		return pTable.contains(new HashBytes(key));
	}

	public HashBytes search(String key) {
		return pTable.search(new HashBytes(key.getBytes()));
	}

	public HashBytes search(byte[] key) {
		return pTable.search(new HashBytes(key));
	}

	public static void main(String[] args) {
		RainbowTable table = new RainbowTable(4096);
		table.add("fdh");
		System.out.println(table.find("fdh"));
	}

}
