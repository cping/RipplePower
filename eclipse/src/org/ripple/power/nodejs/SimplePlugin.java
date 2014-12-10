package org.ripple.power.nodejs;

import javax.script.ScriptContext;

import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.FileUtils;

import java.net.URL;
import java.util.List;

public class SimplePlugin implements JSPlugin {
	private String name;
	private String desc;

	private RhinoEngine rhengine;

	public SimplePlugin(String file, String name, String desc) {
		this.name = name;
		this.desc = desc;
		rhengine = new RhinoEngine(file);
		try {
			rhengine.eval(FileUtils.readAsText(UIRes.getStream(file)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimplePlugin(String file, ScriptContext context) {

	}

	public SimplePlugin(URL url) {

	}

	public Object execute(String function, Object... objects) {
		Object result = null;
		try {
			result = rhengine.invoke(function, objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> getAvailiableFunctions() {
		return null;
	}

	public String getDescription() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public void putValueToContext(String key, Object obj) {
		rhengine.addObject(key, obj);
	}

}
