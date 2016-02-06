#PluginLoader
It can load plugins packaged in .jar by looking in the manifest file to find information.
You can take a look to the [PluginLoader testplugin](https://github.com/hugo4715/PluginLoader TestPlugin) repository to find how plugins are made.


##Loading Plugin
The PluginManager object is a singleton, so you can access it using
```java
PluginManager.getInstance();
```
