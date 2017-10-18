package org.ripple.power.hft;

import java.io.IOException;
import java.util.HashMap;

import org.ripple.power.config.RPConfig;

public class Configuration extends BOT_SET {

	private static HashMap<String, Configuration> _instance = new HashMap<String, Configuration>(10);

	public static Configuration get(String res) {
		String key = res.trim().toLowerCase();
		Configuration result = _instance.get(key);
		if (result == null) {
			_instance.put(key, result = new Configuration(res));
		}
		return result;
	}

	private RPConfig _config;

	public Configuration(String res) {
		try {
			_config = new RPConfig(res);
			if (_config != null) {
				this.operative_amount = _config.getFloatValue("operative_amount");
				this.min_volume = _config.getFloatValue("min_volume");
				this.max_volume = _config.getFloatValue("max_volume");
				this.gateway_address = _config.getValue("gateway_address");
				this.currency_code = _config.getValue("currency_code");
				this.cleanup_zombies = _config.getBoolValue("cleanup_zombies");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValue(String key) {
		return _config.getValue(key);
	}
}
