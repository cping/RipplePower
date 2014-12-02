package org.ripple.power.nodejs;

public class JSBool extends JSValue<Boolean> {
	
	public static final JSBool TRUE = new JSBool(true);
	
	public static final JSBool FALSE = new JSBool(false);
	
	public JSBool (final Boolean value) {
		super(JSType.BOOL,value);
	}

}
