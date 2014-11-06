package org.ripple.power.hft.command.script;

import org.address.collection.ArrayMap;

public class ScriptContext {
	
	private ArrayMap varMap;
	public boolean isGlobal;
	private ScriptContext parent;
	private ScriptExecute host; 

	public ScriptContext(ScriptExecute host) {
		this.parent = null;
		this.host = host;
		this.isGlobal = true;
		this.varMap = new ArrayMap();
	}
	
	public ScriptContext(ScriptContext parent) {
		this.parent = parent;
		host = null;
		isGlobal = false;
		varMap = new ArrayMap();
	}
	
	public boolean hasVar(String name) {
		return varMap.containsKey(name);
	}
	
	public ScriptObject getVar(String name) {
        if (varMap.containsKey(name)) {
            return (ScriptObject) varMap.get(name);
        } else if (parent != null) {
            return parent.getVar(name);
        } else if(host != null){
            return host.getVar(name);
        }
        return ScriptObject.FSUNDEF; 
	}
	
	public void setVar(String name, ScriptObject value) {
		if(parent != null && parent.hasVar(name) == true) {
			parent.setVar(name, value);
		}
		else if(host != null && host.setVar(name, value) == true) {
			return;
		}
		else {
			varMap.put(name, value);
		}
	}
	
	public void setGlobalVar(String name, ScriptObject value) {
		if(host != null && host.setVar(name, value) == true) {
			return;
		}
		else if(parent != null) {
			parent.setVar(name, value);
		}
		else {
			varMap.put(name, value);
		}
	}
	
	public void clear() {
		varMap.clear();
	}
	
}
