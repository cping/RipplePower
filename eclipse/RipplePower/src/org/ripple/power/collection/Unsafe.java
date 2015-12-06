package org.ripple.power.collection;

public interface Unsafe {

	public void throwException(Throwable t);

	public int arrayIndexScale(Class<?> clazz);

	public int arrayBaseOffset(Class<?> clazz);

	public void allocateInstance(Class<?> clazz);

	public long getLong(Object... args);
}