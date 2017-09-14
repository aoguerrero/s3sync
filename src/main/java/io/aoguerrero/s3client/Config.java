package io.aoguerrero.s3client;

import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static Config instance;
	private Properties props;
	
	public static synchronized Config getInstance() throws Exception {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	private Config() throws Exception {
		InputStream input = Main.class.getClassLoader().getResourceAsStream("main.properties");
		props = new Properties();
		props.load(input);
	}
	
	public String getValue(String key) {
		return props.getProperty(key);
	}
	
}
