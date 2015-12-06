package org.ripple.power.nodejs;

import java.util.List;

public interface JSPlugin {

	public String getName();

	public String getDescription();

	public List<String> getAvailiableFunctions();

	public Object execute(String function, Object... objects);

	public void putValueToContext(String key, Object obj);
}
