package fr.hugo4715.pluginloader.api;

import fr.hugo4715.pluginloader.PluginManager;


public abstract class Plugin {
	private PluginInfo infos;
	public Plugin(PluginInfo infos) {
		this.infos = infos;
	}
	
	public abstract boolean onLoad();
	public abstract void onUnload();
	
	public PluginInfo getInfo(){
		return infos;
	}
	
	public void log(String msg){
		PluginManager.getInstance().log(this, msg);
	}
}
