package org.ripple.power.nodejs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JSArray<T extends Object> extends JSValue<ArrayList<T>> implements
		List<T> {

	public JSArray() {
		super(JSType.ARRAY,new ArrayList<T>());
	}

	@SuppressWarnings({ "unchecked" })
	private JSArray(final JSArray<T> toclone) {
		this((ArrayList<T>) toclone.getValue().clone());
	}

	@SuppressWarnings("unchecked")
	protected JSArray(final ArrayList<T> toclone) {
		super(JSType.ARRAY,(ArrayList<T>) toclone.clone());
	}

	public void add(final int index, final T element) {
		getValue().add(index, js(element));
	}

	public boolean add(final T e) {
		return getValue().add(js(e));
	}

	public boolean addAll(final Collection<? extends T> c) {
		return getValue().addAll(c);
	}

	public boolean addAll(final int index, final Collection<? extends T> c) {
		return getValue().addAll(index, c);
	}

	public void clear() {
		getValue().clear();
	}

	@Override
	public Object clone() {
		return new JSArray<T>(this);
	}

	public boolean contains(final Object o) {
		return getValue().contains(o);
	}

	public boolean containsAll(final Collection<?> c) {
		return getValue().containsAll(c);
	}

	public void ensureCapacity(final int minCapacity) {
		getValue().ensureCapacity(minCapacity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof JSArray<?>)) {
			return false;
		}
		return true;
	}

	public T get(final int index) {
		return getValue().get(index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
		return result;
	}

	public int indexOf(final Object o) {
		return getValue().indexOf(o);
	}

	public boolean isEmpty() {
		return getValue().isEmpty();
	}

	public Iterator<T> iterator() {
		return getValue().iterator();
	}

	@SuppressWarnings("unchecked")
	public T js(final T e) {
		if (null == e) {
			return null;
		}
		Object o = e;
		if (!(e instanceof JSValue)) {
			if (e instanceof String) {
				o = new JSString((String) e);
			} else if (e instanceof Number) {
				o = new JSNumber((Number) e);
			} else if (e instanceof Boolean) {
				o = new JSBool((Boolean) e);
			}
		}
		if (!o.getClass().isAssignableFrom(e.getClass())) {
			return e;
		}
		return (T) o;
	}

	public int lastIndexOf(final Object o) {
		return getValue().lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return getValue().listIterator();
	}

	public ListIterator<T> listIterator(final int index) {
		return getValue().listIterator(index);
	}

	public JSArray<T> put(@SuppressWarnings("unchecked") final T... es) {
		for (final T e : es) {
			add(e);
		}
		return this;
	}

	public T remove(final int index) {
		return getValue().remove(index);
	}

	public boolean remove(final Object o) {
		return getValue().remove(o);
	}

	public boolean removeAll(final Collection<?> c) {
		return getValue().removeAll(c);
	}

	public boolean retainAll(final Collection<?> c) {
		return getValue().retainAll(c);
	}

	public T set(final int index, final T element) {
		return getValue().set(index, element);
	}

	public int size() {
		return getValue().size();
	}

	public List<T> subList(final int fromIndex, final int toIndex) {
		return getValue().subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return getValue().toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(final T[] a) {
		return getValue().toArray(a);
	}

	public void trimToSize() {
		getValue().trimToSize();
	}

}
