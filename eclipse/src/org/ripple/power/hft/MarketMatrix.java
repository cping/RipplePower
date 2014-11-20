package org.ripple.power.hft;

import org.ripple.power.collection.ArrayMap;

public class MarketMatrix {
	protected final static long UNSET = -1l;
	private final static long LONG_SIZE_IN_BYTES = 8;
	private final long[] _indexes;
	private final int _rows;
	private final int _columns;
	private int _currShift;
	private ArrayMap _list = new ArrayMap();

	protected MarketMatrix(int r, int c) {
		this._indexes = new long[r];
		this._rows = r;
		this._columns = c;
		this._currShift = 0;
		for (int i = 0; i < r; i++) {
			long startIndex = _list.size();
			_indexes[i] = startIndex;
		}
	}

	protected void set(int row, int column, long value) {
		if (row >= _rows) {
			return;
		}
		if (column >= _columns) {
			return;
		}
		row = shiftedRow(row);
		long offset = calcOffset(row, column);
		_list.put(offset, value);
	}

	protected long get(int row, int column) {
		row = shiftedRow(row);
		long offset = calcOffset(row, column);
		long value = (long) _list.get(offset);
		return value;
	}

	private int shiftedRow(int row) {
		return (row + _currShift) % (_rows);
	}

	public void destroy() {
		for (int i = 0; i < _rows; i++) {
			_list.remove(_rows);
		}
	}

	private long calcOffset(int row, int column) {
		return _indexes[row] + column * LONG_SIZE_IN_BYTES;
	}

	protected void shift() {
		_currShift = (_currShift + 1) % (_indexes.length - 1);
	}

	protected void copy(int dest, int source) {
		for (int i = 0; i < _columns; i++) {
			long value = get(source, i);
			set(dest, i, value);
		}
	}

	public ArrayMap getList() {
		return _list;
	}

}
