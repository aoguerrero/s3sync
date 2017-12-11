package io.aoguerrero.objstoragesync;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Sync<T> {

	private final static String UPDATE_SUFIX = ".update";
	private final static String DELETE_SUFIX = ".delete";
	
	public abstract void start(String localDirName) throws Exception;
	
	public void sync(String localDirName, ObjStorageClient<T> client) throws Exception {
		/* Local files list */
		final List<String> localFileNames = new ArrayList<String>();
		final List<String> excludedFiles = Arrays.asList(Config.getConfig().getValue("exclude").split(","));
		(new File(localDirName)).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (!file.isDirectory()) {
					String fileName = file.getName();
					if (!excludedFiles.contains(fileName)) {
						localFileNames.add(fileName);
						Log.info("[Local file] " + fileName);
					} else {
						Log.info("[Excluded file] " + fileName);
					}

				}
				return false;
			}
		});

		/* Remote files list */
		String directoryName = Config.getConfig().getValue("bucket");
		List<String> remoteFileNames = client.listFiles(directoryName);

		localFileNames.removeAll(remoteFileNames);
		List<String> noDownload = new ArrayList<String>();
		for (String fileName : localFileNames) {
			String filePath = localDirName + File.separator + fileName;
			if (!fileName.endsWith(DELETE_SUFIX) && !fileName.endsWith(UPDATE_SUFIX)) {
				/* Upload */
				client.uploadFile(directoryName, fileName, filePath);
			} else {
				if (fileName.endsWith(UPDATE_SUFIX)) {
					/* Update */
					String fileNameToUpdate = fileName.substring(0, fileName.length() - UPDATE_SUFIX.length());
					client.deleteFile(directoryName, fileNameToUpdate);
					client.uploadFile(directoryName, fileNameToUpdate, filePath);
					(new File(filePath)).renameTo(new File(localDirName + File.separator + fileNameToUpdate));
					noDownload.add(fileNameToUpdate);
				} else if (fileName.endsWith(DELETE_SUFIX)) {
					/* Delete */
					String fileNameToDelete = fileName.substring(0, fileName.length() - DELETE_SUFIX.length());
					client.deleteFile(directoryName, fileNameToDelete);
					(new File(filePath)).delete();
					noDownload.add(fileNameToDelete);
				}
			}
		}
		remoteFileNames.removeAll(noDownload);

		/* Download */
		Boolean enableDownload = Config.getConfig().getValue("download").equals("true");
		
		if(enableDownload) {
			for (String fileName : remoteFileNames) {
				String filePath = localDirName + File.separator + fileName;
				File file = new File(filePath);
				if (!file.exists()) {
					client.downloadFile(directoryName, fileName, filePath);
				}
			}
		} else {
			Log.info("[Download disabled]");
		}

		Log.info("[Finish]");
	}

}
