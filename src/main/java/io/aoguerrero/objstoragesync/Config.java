package io.aoguerrero.objstoragesync;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	private static Config instance;
	private Properties props;

	public static synchronized Config getInstance() throws Exception {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	private Config() throws Exception {

		String propertiesFileName = new java.io.File(
				Config.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
		propertiesFileName = propertiesFileName.substring(0, propertiesFileName.length()-4)+".properties";

		FileInputStream input = new FileInputStream(
				new File(System.getProperty("user.dir") + File.separator + propertiesFileName));
		props = new Properties();
		props.load(input);
		input.close();
	}

	public String getValue(String key) {
		return props.getProperty(key);
	}

}
