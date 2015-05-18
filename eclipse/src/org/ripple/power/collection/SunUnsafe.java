package org.ripple.power.collection;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.ripple.power.utils.ReflectorUtils;

public class SunUnsafe implements Unsafe {
	private Object sunUnsafe;

	public SunUnsafe() {
		try {
			this.sunUnsafe = AccessController
					.doPrivileged(new PrivilegedAction<Object>() {
						@Override
						public Object run() {
							try {
								Class<?> unsafeClass = Class
										.forName("sun.misc.Unsafe");
								Field field = unsafeClass
										.getDeclaredField("theUnsafe");
								field.setAccessible(true);
								return (Object) field.get(null);
							} catch (Exception e) {
								throw new Error();
							}
						}
					});

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long getLong(Object... args) {
		return (int) ReflectorUtils.getNotPrefixInvoke(sunUnsafe, "getLong",
				args);
	}

	public int arrayIndexScale(Class<?> clazz) {
		return (int) ReflectorUtils.getNotPrefixInvoke(sunUnsafe,
				"arrayIndexScale", new Object[] { clazz });
	}

	public int arrayBaseOffset(Class<?> clazz) {
		return (int) ReflectorUtils.getNotPrefixInvoke(sunUnsafe,
				"arrayBaseOffset", new Object[] { clazz });
	}

	public void allocateInstance(Class<?> clazz) {
		ReflectorUtils.getNotPrefixInvoke(sunUnsafe, "allocateInstance",
				new Object[] { clazz });
	}

	public void throwException(Throwable t) {
		ReflectorUtils.getNotPrefixInvoke(sunUnsafe, "throwException",
				new Object[] { t });
	}
}