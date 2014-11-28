package org.ripple.power.collection;

import java.util.Arrays;

import org.ripple.power.utils.CollectionUtils;
import org.ripple.power.utils.MathUtils;

public class IntArray {
	public int[] items;
	public int length;
	public boolean ordered;

	public IntArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public IntArray(int capacity) {
		this(true, capacity);
	}

	public IntArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new int[capacity];
	}

	public IntArray(IntArray array) {
		this.ordered = array.ordered;
		length = array.length;
		items = new int[length];
		System.arraycopy(array.items, 0, items, 0, length);
	}

	public IntArray(int[] array) {
		this(true, array, 0, array.length);
	}

	public IntArray(boolean ordered, int[] array, int startIndex, int count) {
		this(ordered, count);
		length = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void unshift(int value) {
		if (length > 0) {
			int[] items = this.items;
			int[] newItems = new int[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
		} else {
			add(value);
		}
	}

	public void push(int value) {
		add(value);
	}

	public void add(int value) {
		int[] items = this.items;
		if (length == items.length) {
			items = relength(Math.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(IntArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(IntArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new IllegalArgumentException(
					"offset + length must be <= length: " + offset + " + "
							+ length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(int... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(int[] array, int offset, int length) {
		int[] items = this.items;
		int lengthNeeded = length + length;
		if (lengthNeeded > items.length)
			items = relength(Math.max(8, (int) (lengthNeeded * 1.75f)));
		System.arraycopy(array, offset, items, length, length);
		length += length;
	}

	public int get(int index) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		return items[index];
	}

	public void set(int index, int value) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		items[index] = value;
	}

	public void incr(int index, int value) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		items[index] += value;
	}

	public void mul(int index, int value) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		items[index] *= value;
	}

	public void insert(int index, int value) {
		if (index > length)
			throw new IndexOutOfBoundsException("index can't be > length: "
					+ index + " > " + length);
		int[] items = this.items;
		if (length == items.length)
			items = relength(Math.max(8, (int) (length * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, length - index);
		else
			items[length] = items[index];
		length++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new IndexOutOfBoundsException("first can't be >= length: "
					+ first + " >= " + length);
		if (second >= length)
			throw new IndexOutOfBoundsException("second can't be >= length: "
					+ second + " >= " + length);
		int[] items = this.items;
		int firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(int value) {
		int i = length - 1;
		int[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(int value) {
		int[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(int value) {
		int[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(int value) {
		int[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public int removeIndex(int index) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		int[] items = this.items;
		int value = items[index];
		length--;
		if (ordered) {
			System.arraycopy(items, index + 1, items, index, length - index);
		} else {
			items[index] = items[length];
		}
		return value;
	}

	public void removeRange(int start, int end) {
		if (end >= length) {
			throw new IndexOutOfBoundsException("end can't be >= length: "
					+ end + " >= " + length);
		}
		if (start > end) {
			throw new IndexOutOfBoundsException("start can't be > end: "
					+ start + " > " + end);
		}
		int[] items = this.items;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(items, start + count, items, start, length
					- (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		length -= count;
	}

	public boolean removeAll(IntArray array) {
		int length = this.length;
		int startlength = length;
		int[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			int item = array.get(i);
			for (int ii = 0; ii < length; ii++) {
				if (item == items[ii]) {
					removeIndex(ii);
					length--;
					break;
				}
			}
		}
		return length != startlength;
	}

	public int pop() {
		return items[--length];
	}

	public int shift() {
		return removeIndex(0);
	}

	public int peek() {
		return items[length - 1];
	}

	public int first() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return items[0];
	}

	public void clear() {
		length = 0;
	}

	public int[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public int[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(Math.max(8, lengthNeeded));
		return items;
	}

	protected int[] relength(int newlength) {
		int[] newItems = new int[newlength];
		int[] items = this.items;
		System.arraycopy(items, 0, newItems, 0,
				Math.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public void sort() {
		Arrays.sort(items, 0, length);
	}

	public void reverse() {
		int[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			int temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle() {
		int[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			int temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public int random() {
		if (length == 0)
			return 0;
		return items[MathUtils.random(0, length - 1)];
	}

	public int[] toArray() {
		int[] array = new int[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof IntArray))
			return false;
		IntArray array = (IntArray) object;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}

	public String toString(String separator) {
		if (length == 0)
			return "";
		int[] items = this.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
			buffer.append(separator);
			buffer.append(items[i]);
		}
		return buffer.toString();
	}

	static public IntArray with(int... array) {
		return new IntArray(array);
	}

	public static int[] slice(int[] array, int begin, int end) {
		if (begin > end) {
			throw new RuntimeException();
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		int[] ret = new int[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static int[] slice(int[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public IntArray slice(int length) {
		return new IntArray(slice(this.items, length));
	}

	public IntArray slice(int begin, int end) {
		return new IntArray(slice(this.items, begin, end));
	}

	public static int[] concat(int[] array, int[] other) {
		int[] ret = new int[array.length + other.length];
		System.arraycopy(array, 0, ret, 0, array.length);
		System.arraycopy(other, 0, ret, array.length, other.length);
		return ret;
	}

	public IntArray concat(IntArray o) {
		return new IntArray(concat(this.items, o.items));
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		int[] items = this.items;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
			buffer.append(split);
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public String toString() {
		return toString(',');
	}
}
