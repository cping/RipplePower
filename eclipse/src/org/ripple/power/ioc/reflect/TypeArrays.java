package org.ripple.power.ioc.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.ripple.power.utils.ReflectorUtils;

public class TypeArrays extends TypeArray {

	public static final String CONSTRUCTOR_METHOD_NAME = "<init>";

	private final String name;

	private final int hashCode;

	public static TypeArrays getNamedTypeArray(Method method) {
		return new TypeArrays(method, method.getParameterTypes());
	}

	public static TypeArrays getNamedTypeArray(Constructor<?> constructor) {
		return new TypeArrays(CONSTRUCTOR_METHOD_NAME,
				constructor.getParameterTypes());
	}

	public TypeArrays(Method method, Class<?>[] types) {
		this(ReflectorUtils.getMethodName(method), types);
	}

	public TypeArrays(String methodName, Class<?>[] types) {
		super(types);
		this.name = methodName;
		this.hashCode = ((super.hashCode() * 17) + (name.hashCode() * 31));
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		if (obj != null && TypeArrays.class.equals(obj.getClass())) {
			TypeArrays other = (TypeArrays) obj;
			return (this.name.equals(other.name) && Arrays.equals(this.types,
					other.types));
		}
		return false;
	}

	public int hashCode() {
		return hashCode;
	}

}
