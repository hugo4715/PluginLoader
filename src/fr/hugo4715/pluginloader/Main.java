package fr.hugo4715.pluginloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PluginManager.getInstance().setUseLogFile(true);
		PluginManager.getInstance().loadPlugins(new File("plugins/"));
		PluginManager.close();
	}
}
