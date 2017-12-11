package io.aoguerrero.objstoragesync;

import java.io.File;

import io.aoguerrero.objstoragesync.s3.S3Sync;

public class Main {

	public static void main(String[] args) throws Exception {

		String homeDir = System.getProperty("user.dir");

		if (args.length > 0) {
			for (String arg : args) {
				if (arg.startsWith("--home=")) {
					homeDir = arg.split("=")[1];
				}
			}
		}
		
		File fHomeDir = new File(homeDir);
		if (!fHomeDir.exists() || !fHomeDir.isDirectory()) {
			Log.error("Directorio de inicio no válido '" + homeDir + "'");
			return;
		}
		Config.init(homeDir);		

		S3Sync sync = new S3Sync();
		sync.start(homeDir);
	}
}
