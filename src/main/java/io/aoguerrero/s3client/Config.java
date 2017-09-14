package io.aoguerrero.s3client;

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
		FileInputStream input = new FileInputStream(
				new File(System.getProperty("user.dir") + File.separator + "sync.properties"));
		props = new Properties();
		props.load(input);
		input.close();
	}

	public String getValue(String key) {
		return props.getProperty(key);
	}

}
