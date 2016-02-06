#PluginLoader
###[Javadoc is here](http://hugo4715.github.io/PluginLoader/doc/index.html)

###This software is under [Creative commons](http://creativecommons.org/licenses/by/4.0/) (The comple license is [here](http://creativecommons.org/licenses/by/4.0/legalcode))
It can load plugins packaged in .jar by looking in the manifest file to find information.
You can take a look to the [PluginLoader testplugin](https://github.com/hugo4715/PluginLoader TestPlugin) repository to find how plugins are made.


##Loading Plugin
The PluginManager object is a singleton, so you can access it using
```java
PluginManager.getInstance();
```
You can set if you want to use logs using this line. It will create a log folder and save logs as zip files.
```java
PluginManager.getInstance().setUseLogFile(true);
```
You can then load plugins using 
```java
PluginManager.getInstance().loadPlugins(new File("plugins/"));
```
It will load all plugins in the specified folder and call their `onLoad()` method.
If you just want to load one plugin, you can also use 
```java
PluginManager.getInstance().loadPlugin(new File("plugins/myPlugin.jar"));
```

At the end you must use this static metohd 
```java
PluginManager.close();
```
It will call the `onUnload()`  then unload the plugins and destroy the PluginManager instance.


So, in your plugin main you should have
```java
PluginManager.getInstance().setUseLogFile(true);
PluginManager.getInstance().loadPlugins(new File("plugins/"));
PluginManager.close();
```
