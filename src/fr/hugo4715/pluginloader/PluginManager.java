package fr.hugo4715.pluginloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.hugo4715.pluginloader.Utils.FileNameFilterUtils;
import fr.hugo4715.pluginloader.api.Plugin;
import fr.hugo4715.pluginloader.api.PluginInfo;

public class PluginManager {
	private static PluginManager instance;
	public static PluginManager getInstance(){
		if(instance == null){
			instance = new PluginManager();
		}
		return instance;
	}
	private List<Plugin> plugins; //The list of plugins
	private double pluginManagerVersion = 1.0;
	private PrintStream logFile;

	//Options
	public boolean useLog = false;

	private PluginManager() {
		plugins = new ArrayList<Plugin>();
	}
	/*
	 * Internal things
	 */
	private void log(String msg){
		Date d = new Date();
		System.out.println(d.getHours() + "/" + d.getMinutes() + "/" + d.getSeconds() +": "+ msg);
		if(logFile != null){
			logFile.println(d.getHours() + "/" + d.getMinutes() + "/" + d.getSeconds() +": "+ msg);
			logFile.flush();
		}
	}


	/*
	 * 
	 * Public API 
	 * 
	 */
	
	public static void close(){
		PluginManager.getInstance().unloadAllPlugins();
		PluginManager.getInstance().flushLogs();
		PluginManager.instance = null;
		
	}

	/**
	 * explain itself nah?
	 * @param useLog
	 */
	public void setUseLogFile(boolean useLog){
		if(useLog && logFile == null){
			try {
				File f = new File("logs/log.txt");
				new File("logs").mkdirs();
				f.createNewFile();
				logFile = new PrintStream(f);
				Date d = new Date();
				logFile.println("Log File from " + d.toLocaleString());
				logFile.println();
				logFile.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(!useLog && logFile != null){
			logFile.flush();
			logFile.close();
			logFile = null;
		}
	}

	public void flushLogs(){
		if(logFile == null)return;
		try{
			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream("logs/" + new Date().toLocaleString());
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze= new ZipEntry("log.txt");
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream("logs/log.txt");

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();

			zos.close();

		}catch(Exception e){
			log("Error while saving log file:");
			e.printStackTrace();
		}
	}

		/**
		 * Used by plugins to log info to the console
		 * @param plugin
		 * @param msg
		 */
		public void log(Plugin plugin, String msg){
			log("[" + plugin.getInfo().getName() + "] " + msg);
		}
		/**
		 * 
		 * @return The version of the plugin loader
		 */
		public double getPluginManagerVersion(){
			return pluginManagerVersion;
		}

		/**
		 * Load all plugins in a directory
		 * @param pluginDirectory 
		 */
		public void loadPlugins(File pluginDirectory){
			for(File f : pluginDirectory.listFiles(FileNameFilterUtils.getExtensionFilter(".jar"))){
				log("Trying to load " + f.getName());
				loadPlugin(f);
			}
		}

		/**
		 * Load a plugin from the file
		 * @param pluginPath
		 */
		public boolean loadPlugin(File pluginPath){
			try{
				URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader(); 
				Class<URLClassLoader> classLoaderClass = URLClassLoader.class; 

				Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class}); 
				method.setAccessible(true); 
				method.invoke(systemClassLoader, new Object[]{pluginPath.toURL()});
				classLoaderClass = null;
				systemClassLoader = null;

				JarFile jar = new JarFile(pluginPath);
				String mainClass = jar.getManifest().getMainAttributes().getValue("EntryPoint");

				ClassLoader loader = new URLClassLoader(new URL[] { pluginPath.toURI().toURL() }, this.getClass().getClassLoader());
				if(mainClass == null){
					log("Error: Manifest does not contain entry point.");
					return false;
				}
				Class<?> clazz = loader.loadClass(mainClass);

				Class[] argsType = {PluginInfo.class};
				Constructor<?> construc = clazz.getDeclaredConstructor(argsType);
				if(construc == null){
					log("Error: No constructor found.");
					return false;
				}
				Object[] params = {new PluginInfo(jar.getManifest())};
				Plugin plugin = (Plugin) construc.newInstance(params);
				if(plugin.onLoad()){
					log("Successfully loaded " + plugin.getInfo().getName());
					plugins.add(plugin);
				}else{
					log("Failed to load " + plugin.getInfo().getName());
				}
				return true;
			}catch(Exception e){
				log("Error:");
				e.printStackTrace();
				return false;
			}
		}

		/**
		 * Unload the given plugin
		 * @param plugin
		 */
		public void unloadPlugin(Plugin plugin){
			plugin.onUnload();
			plugins.remove(plugin);
			log("Successfully unloaded " + plugin.getInfo().getName());
		}

		/**
		 * Unload all plugins
		 */
		public void unloadAllPlugins(){
			for(Plugin p : plugins){
				p.onUnload();
			}
			plugins.clear();
			log("Unloaded all plugins");
		}

		/**
		 * Return an array containing all plugins
		 * @return
		 */
		public Plugin[] getPlugins(){
			return plugins.toArray(new Plugin[plugins.size()]);
		}
		/**
		 * Return a plugin by name
		 * @param name The plugin, or null if there is no such plugin
		 */
		public Plugin getPlugin(String name){
			for(Plugin p : plugins){
				if(p.getInfo().getName().equalsIgnoreCase(name))return p;
			}
			return null;
		}

		/**
		 * Return a plugin matching name and version
		 * @param name
		 * @param version
		 */
		public Plugin getPlugin(String name,double version){
			for(Plugin p : plugins){
				if(p.getInfo().getVersion() == version && p.getInfo().getName().equalsIgnoreCase(name) )return p;
			}
			return null;
		}
	}
