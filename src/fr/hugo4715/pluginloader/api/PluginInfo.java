package fr.hugo4715.pluginloader.api;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class PluginInfo {
	private double version;
	private String name;
	private String webSite;
	private String author;
	private String description;
	
	public PluginInfo(Manifest m) {
		Attributes a = m.getMainAttributes();
		try{
			name = a.getValue("Name");
			if(a.containsKey("WebSite"))webSite = a.getValue("WebSite");
			if(a.containsKey("Author"))author = a.getValue("Author");
			if(a.containsKey("Description"))description = a.getValue("Description");
			version = Double.parseDouble(a.getValue("Version"));
		}catch(Exception e){
			version = -1;
		}
		
	}
	
	public double getVersion() {
		return version;
	}
	public String getName() {
		return name;
	}
	public String getWebSite() {
		return webSite;
	}
	public String getAuthor() {
		return author;
	}
	public String getDescription() {
		return description;
	}
	
	
}
