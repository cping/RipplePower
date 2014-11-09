package org.address.collection;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.ripple.power.ioc.reflect.ConstructorConverter;
import org.ripple.power.ioc.reflect.Converter;
import org.ripple.power.ioc.reflect.PropertyEditorConverter;
import org.ripple.power.ioc.reflect.Reflector;
import org.ripple.power.ioc.reflect.TypeArray;


public class ConverterMap {

	public static final BaseTypeMap BaseTypeMap = new BaseTypeMap();

	private Map converters;

	public ConverterMap() {
		super();
		this.converters = new HashMap();
	}

	public Converter lookup(Class target, Class input) {
		Converter converter = (Converter) converters.get(new TypeArray(
				new Class[] { target, input }));
		if (converter == null) {
			TypeArray[] typearray = (TypeArray[]) converters.keySet().toArray(
					new TypeArray[converters.size()]);
			for (int i = 0; i < typearray.length; i++) {
				if (typearray[i].getParameterTypes()[0].equals(target)
						&& typearray[i].getParameterTypes()[1]
								.isAssignableFrom(input)) {
					converter = (Converter) converters.get(typearray[i]);
					TypeArray array = new TypeArray(
							new Class[] { target, input });
					array.setAliased(true);
					converters.put(array, converter);
					break;
				}
			}
		}
		return converter;
	}

	public void store(Class targetClass, Converter converter) {
		converters.put(new TypeArray(new Class[] { targetClass,
				converter.getInputType() }), converter);
	}

	public Object[] convertParameters(Class[] targetTypes, Object[] parameters) {
		if (parameters == null) {
			return null;
		}
		Object[] converted = new Object[parameters.length];
		for (int i = 0; i < converted.length; i++) {

			if (convertAsNull(converted, parameters, i))
				continue;

			if (convertAsAssignable(targetTypes, converted, parameters, i))
				continue;

			if (convertAsConvertable(targetTypes, converted, parameters, i))
				continue;

			if (convertAsPropertyEditor(targetTypes, converted, parameters, i))
				continue;

			if (convertAsPrimitive(targetTypes, converted, parameters, i))
				continue;

			if (convertAsConstructor(targetTypes, converted, parameters, i))
				continue;

		}

		return converted;
	}

	public boolean typeAssignable(Class targetType, Class rawClass) {

		if (rawClass == null)
			return true;

		if (targetType.isAssignableFrom(rawClass))
			return true;

		if (lookup(targetType, rawClass) != null)
			return true;

		//if (findPropertyEditor(targetType, rawClass) != null)
		//	return true;

		if (isBaseAssignable(targetType, rawClass))
			return true;

		if (constructorConverter(targetType, rawClass) != null)
			return true;

		return false;
	}

	public boolean typesAssignable(Class[] targetTypes, Class[] rawTypes) {
		if ((rawTypes == null || rawTypes.length == 0)
				&& (targetTypes == null || targetTypes.length == 0)) {
			return true;
		}

		if (rawTypes.length != targetTypes.length) {
			return false;
		}

		for (int i = 0; i < targetTypes.length; i++) {
			if (!typeAssignable(targetTypes[i], rawTypes[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean convertAsNull(Object[] converted, Object[] parameters, int i) {
		if (parameters[i] == null) {
			converted[i] = null;
			return true;
		}
		return false;
	}

	private boolean convertAsAssignable(Class[] targetTypes,
			Object[] converted, Object[] parameters, int i) {
		if (targetTypes[i].isAssignableFrom(parameters[i].getClass())) {
			converted[i] = parameters[i];
			return true;
		}
		return false;
	}

	private boolean convertAsPrimitive(Class[] targetTypes, Object[] converted,
			Object[] parameters, int i) {
		if (isBaseAssignable(targetTypes[i], parameters[i].getClass())) {
			converted[i] = parameters[i];
			return true;
		}
		return false;
	}

	private boolean isBaseAssignable(Class targetType, Class parameterClass) {
		if (targetType.isPrimitive()) {

			Class parameterPrimitiveClass = BaseTypeMap
					.getBaseClassForWrapper(parameterClass);

			if (targetType == parameterPrimitiveClass) {
				return true;
			}
		}
		return false;
	}

	private boolean convertAsPropertyEditor(Class[] targetTypes,
			Object[] converted, Object[] parameters, int i) {
		Converter pec = findPropertyEditor(targetTypes[i], parameters[i]
				.getClass());
		if (pec != null) {
			converted[i] = pec.convert(parameters[i]);
			return true;
		}
		return false;
	}

	private Converter findPropertyEditor(Class targetType, Class parameterClass) {

		Converter pec = null;
		if (String.class.equals(parameterClass)) {
			PropertyEditor pe = PropertyEditorManager.findEditor(targetType);
			if (pe != null) {
				pec = new PropertyEditorConverter(pe);
				store(targetType, pec);
			}
		}
		return pec;
	}

	private boolean convertAsConvertable(Class[] targetTypes,
			Object[] converted, Object[] parameters, int i) {
		Converter converter = lookup(targetTypes[i], parameters[i].getClass());
		if (converter != null) {
			converted[i] = converter.convert(parameters[i]);
			return true;
		}
		return false;
	}

	private boolean convertAsConstructor(Class[] targetTypes,
			Object[] converted, Object[] parameters, int i) {
		Converter cc = constructorConverter(targetTypes[i], parameters[i]
				.getClass());
		if (cc != null) {
			converted[i] = cc.convert(parameters[i]);
			return true;
		}
		return false;
	}

	private Converter constructorConverter(Class targetType,
			Class parameterClass) {
		Converter cc = null;
		Constructor c = Reflector.getReflector(targetType).lookupConstructor(
				new Class[] { parameterClass }, this, false);
		if (c != null) {
			cc = new ConstructorConverter(parameterClass, c);
			store(targetType, cc);
		}
		return cc;
	}

	public static class BaseTypeMap {

		private BaseTypeMap() {
			super();
		}

		private final Class[] wrapperClasses = new Class[] { Boolean.class,
				Byte.class, Character.class, Short.class, Integer.class,
				Long.class, Float.class, Double.class };

		private final Class[] baseClasses = new Class[] { Boolean.TYPE,
				Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE,
				Float.TYPE, Double.TYPE };

		public Class getBaseClassForWrapper(Class wrapperClass) {
			for (int i = 0; i < wrapperClasses.length; i++) {
				if (wrapperClasses[i] == wrapperClass) {
					return baseClasses[i];
				}
			}
			return null;
		}

		public Class getWrapperClassForBase(Class BaseClass) {
			for (int i = 0; i < baseClasses.length; i++) {
				if (baseClasses[i] == BaseClass) {
					return wrapperClasses[i];
				}
			}
			return null;
		}

		public Class getBaseClassForName(String name) {
			for (int i = 0; i < baseClasses.length; i++) {
				if (baseClasses[i].getName().equals(name)) {
					return baseClasses[i];
				}
			}
			return null;
		}
	}
}
