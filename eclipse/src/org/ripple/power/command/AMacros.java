package org.ripple.power.command;

import org.json.JSONObject;
import org.ripple.power.txns.CurrencyUtils;

public abstract class AMacros implements IMacros {

	protected String clazz;

	protected final String[] commands;

	protected final String[] command_types;

	protected boolean syncing;

	protected DMacros macros;

	protected IScriptLog log;

	protected int line;

	protected void error(Exception ex) {
		if (log != null) {
			log.err("line: %s\nexception %s", line, ex.getMessage());
		}
	}

	protected void setMacros(DMacros m) {
		this.macros = m;
	}

	protected void setScriptLog(IScriptLog l) {
		this.log = l;
	}

	protected void setLine(int l) {
		this.line = l;
	}

	protected void setConfig(IScriptLog l, DMacros m, int line) {
		setMacros(m);
		setScriptLog(l);
		setLine(line);
	}

	public AMacros(String clazz, String[] cmdtables, String[] cmdtypes) {
		this.clazz = clazz;
		this.commands = cmdtables;
		this.command_types = cmdtypes;
	}

	protected void log(int type, JSONObject res) {
		if (log != null && res != null) {
			log.info(clazz + getCommandName(type));
			log.info(res.toString());
		}
	}

	protected void log(int type, String message) {
		if (log != null && message != null) {
			log.info(clazz + getCommandName(type));
			log.info(message);
		}
	}

	public boolean isSyncing() {
		return syncing;
	}

	protected void setSyncing(int type, boolean sy) {
		syncing = sy;
		macros.setVariable(clazz + getCommandName(type) + ".syncing", syncing);
	}

	protected void setJsonArrayVar(int type, JSONObject obj, String name,
			int idx, String key) {
		setJsonArrayVar(type, obj, name, idx, key, false);
	}

	protected void setJsonArrayVar(int type, JSONObject obj, String name,
			int idx, String key, boolean useCur) {
		String result = name + "[" + idx + "]" + "." + key;
		if (obj.has(key)) {
			if (useCur) {
				setVar(type, result, CurrencyUtils.getAmount(obj.get(key)));
			} else {
				setVar(type, result, obj.get(key));
			}
		} else {
			setVar(type, result, null);
		}
	}

	protected void setJsonVar(int type, JSONObject obj, String key) {
		if (obj.has(key)) {
			setVar(type, key, obj.get(key));
		} else {
			setVar(type, key, null);
		}
	}

	protected void setVar(int type, String key, Object value) {
		setVar(type, key, value, false);
	}

	protected void setVar(int type, Object value) {
		setVar(type, null, value, true);
	}
	
	protected void setVar(int type, String key, Object value, boolean useThis) {
		if (macros != null) {
			if (useThis) {
				macros.setVariable(clazz + getCommandName(type), value);
			} else {
				macros.setVariable(clazz + getCommandName(type) + "." + key,
						value);
			}
		}
	}

	protected String getCommandName(int id) {
		if (commands == null) {
			return null;
		}
		if (id > -1 && id < commands.length) {
			return commands[id];
		}
		return null;
	}

	protected int lookupCommand(String str) {
		if (commands == null) {
			return -1;
		}
		str = str.toLowerCase().trim();
		for (int i = 0; i < commands.length; i++) {
			if (str.equals(commands[i])) {
				return i;
			}
		}
		return -1;
	}

	protected int lookupCommandType(String str) {
		if (command_types == null) {
			return -1;
		}
		str = str.toLowerCase().trim();
		for (int i = 0; i < command_types.length; i++) {
			if (str.equals(command_types[i])) {
				return i;
			}
		}
		return -1;
	}
}
