package org.ripple.power.command;

import org.json.JSONObject;

public abstract class AMacros implements IMacros{
	protected String clazz;

		protected final String[] commands;
		
		protected final String[] command_types;

		protected boolean syncing;

		protected DMacros macros;

		protected IScriptLog log;

		public void setMacros(DMacros m) {
			this.macros = m;
		}

		public void setScriptLog(IScriptLog l) {
			this.log = l;
		}

	public AMacros(String clazz,String[] cmdtables,String[] cmdtypes){
		this.clazz =clazz;
		this.commands = cmdtables;
		this.command_types = cmdtypes;
	}

	protected void log(int type, JSONObject res) {
		if (log != null && res != null) {
			log.info(clazz + getCommandName(type));
			log.info(res.toString());
		}
	}

	protected void setSyncing(int type, boolean sy) {
		syncing = sy;
		macros.setVariable(clazz + getCommandName(type) + ".syncing", syncing);
	}

	protected void setJsonVar(int type, JSONObject obj, String key) {
		setVar(type, key, obj.get(key));
	}

	protected void setVar(int type, String key, Object value) {
		if (macros != null) {
			macros.setVariable(clazz + getCommandName(type) + "." + key, value);
		}
	}

	protected String getCommandName(int id) {
		if (id > -1 && id < commands.length) {
			return commands[id];
		}
		return null;
	}

	protected int lookupCommand(String str) {
		str = str.toLowerCase().trim();
		for (int i = 0; i < commands.length; i++) {
			if (str.equals(commands[i])) {
				return i;
			}
		}
		return -1;
	}

	protected int lookupCommandType(String str) {
		str = str.toLowerCase().trim();
		for (int i = 0; i < command_types.length; i++) {
			if (str.equals(command_types[i])) {
				return i;
			}
		}
		return -1;
	}
}
