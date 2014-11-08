package org.ripple.power.command;

public class DefScriptLog implements IScriptLog {

	@Override
	public void err(String mes, Object... o) {
		if (o != null && o.length > 0) {
			System.err.println(String.format(mes, o));
		} else {
			System.err.println(mes);
		}
	}

	@Override
	public void info(String mes, Object... o) {
		if (o != null && o.length > 0) {
			System.out.println(String.format(mes, o));
		} else {
			System.out.println(mes);
		}
	}

	@Override
	public void err(Object mes) {
		System.err.println(mes);
	}

	@Override
	public void info(Object mes) {
		System.out.println(mes);
	}
	
	@Override
	public void line(Object mes) {
		System.out.print(mes);
	}
	
	@Override
	public void newline() {
		System.out.println();
	}

}
