package org.ripple.power.nodejs;

import java.util.List;

public interface JSPluginManager {

    public void activate(JSPlugin plugin);

    public void deactivate(JSPlugin plugin);
    
    public void install(JSPlugin plugin);
    
    public void uninstall(JSPlugin plugin);

    public List<JSPlugin> listPlugins();

    public JSPlugin getPlugin(String name);

    public void removePlugin(String name);

    public int getPluginNumber();
}
