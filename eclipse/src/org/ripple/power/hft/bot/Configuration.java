package org.ripple.power.hft.bot;

import java.io.IOException;
import java.util.HashMap;

import org.ripple.power.config.RPConfig;

public class Configuration {

	private static HashMap<String, Configuration> _instance = new HashMap<String, Configuration>(
			10);

	public static Configuration get(String res) {
		String key = res.trim().toLowerCase();
		Configuration result = _instance.get(key);
		if (result == null) {
			_instance.put(key, result = new Configuration(res));
		}
		return result;
	}

	public static Configuration getRipple() {
		return get("bot/ripple.txt");
	}

	public static Configuration getBTC38() {
		return get("bot/btc38.txt");
	}

	private RPConfig _config;

	public Configuration(String res) {
		try {
			_config = new RPConfig(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValue(String key) {
		return _config.getValue(key);
	}
}
