package fr.hugo4715.pluginloader.Utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilterUtils {
	public static FilenameFilter getExtensionFilter(final String extension){
		return new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(extension);
			}
		};
	}
}
