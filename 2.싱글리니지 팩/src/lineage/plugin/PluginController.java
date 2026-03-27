package lineage.plugin;

public final class PluginController {
	
	static private Plugin plugin;
	
	static public void setPlugin(Plugin plugin){
		PluginController.plugin = plugin;
	}
	
	static public Object init(Class<?> c, Object ... opt){
		if(plugin != null)
			return plugin.init(c, opt);
		
		return null;
	}
	
	static public void toTimer(long time){
		
	}
}
