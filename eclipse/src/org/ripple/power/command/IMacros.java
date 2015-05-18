package org.ripple.power.command;

public interface IMacros {

	public void call(IScriptLog log, int scriptLine, DMacros macros,
			String message);

	public boolean isSyncing();

}
