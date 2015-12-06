package org.ripple.power.command;

public class DefScriptLog implements IScriptLog {

	private boolean _show = true;

	@Override
	public void err(String mes, Object... o) {
		if (!_show) {
			return;
		}
		if (o != null && o.length > 0) {
			System.err.println(String.format(mes, o));
		} else {
			System.err.println(mes);
		}
	}

	@Override
	public void info(String mes, Object... o) {
		if (!_show) {
			return;
		}
		if (o != null && o.length > 0) {
			System.out.println(String.format(mes, o));
		} else {
			System.out.println(mes);
		}
	}

	@Override
	public void err(Object mes) {
		if (!_show) {
			return;
		}
		System.err.println(mes);
	}

	@Override
	public void info(Object mes) {
		if (!_show) {
			return;
		}
		System.out.println(mes);
	}

	@Override
	public void line(Object mes) {
		if (!_show) {
			return;
		}
		System.out.print(mes);
	}

	@Override
	public void newline() {
		if (!_show) {
			return;
		}
		System.out.println();
	}

	@Override
	public void show(boolean flag) {
		_show = flag;
	}

}
