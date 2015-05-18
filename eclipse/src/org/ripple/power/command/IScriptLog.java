package org.ripple.power.command;

public interface IScriptLog {

	public void show(boolean flag);

	public void err(Object mes);

	public void info(Object mes);

	public void line(Object mes);

	public void newline();

	public void err(String mes, Object... o);

	public void info(String mes, Object... o);
}
