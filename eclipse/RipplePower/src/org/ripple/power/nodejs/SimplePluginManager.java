package org.ripple.power.nodejs;

import java.util.ArrayList;
import java.util.List;

public class SimplePluginManager implements JSPluginManager {
	private List<JSPlugin> plist;
	private static SimplePluginManager instance;

	public static SimplePluginManager getInstance() {
		if (instance == null) {
			instance = new SimplePluginManager();
		}
		return instance;
	}

	private SimplePluginManager() {
		plist = new ArrayList<JSPlugin>(1);
	}

	public void activate(JSPlugin plugin) {

	}

	public void deactivate(JSPlugin plugin) {

	}

	public JSPlugin getPlugin(String name) {
		for (JSPlugin p : plist) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public void install(JSPlugin plugin) {
		plist.add(plugin);
	}

	public List<JSPlugin> listPlugins() {
		return plist;
	}

	public void removePlugin(String name) {
		for (int i = 0; i < plist.size(); i++) {
			if (plist.get(i).getName().equals(name)) {
				plist.remove(i);
				break;
			}
		}
	}

	public void uninstall(JSPlugin plugin) {
		plist.remove(plugin);
	}

	public int getPluginNumber() {
		return plist.size();
	}

}
