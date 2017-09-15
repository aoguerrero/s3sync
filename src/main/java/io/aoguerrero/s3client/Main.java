package io.aoguerrero.s3client;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class Main {
	
	private final static String UPDATE_SUFIX = ".update";
	private final static String DELETE_SUFIX = ".delete";
	

	public static void main(String[] args) throws Exception {

		/* S3 Connection */
		BasicAWSCredentials credentials = new BasicAWSCredentials(Config.getInstance().getValue("accessKey"),
				Config.getInstance().getValue("secretKey"));
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Config.getInstance().getValue("region")).build();
		S3Client s3Client = new S3Client(amazonS3);

		/* Local files list */
		String localDirName = System.getProperty("user.dir");
		final List<String> localFileNames = new ArrayList<String>();
		final List<String> excludedFiles = Arrays.asList(Config.getInstance().getValue("exclude").split(","));		
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
		String directoryName = Config.getInstance().getValue("bucket");
		List<String> remoteFileNames = s3Client.listFiles(directoryName);

		localFileNames.removeAll(remoteFileNames);
		List<String> noDownload = new ArrayList<String>();
		for (String fileName : localFileNames) {
			String filePath = localDirName + File.separator + fileName;
			if (!fileName.endsWith(DELETE_SUFIX) && !fileName.endsWith(UPDATE_SUFIX)) {
				/* Upload */
				s3Client.uploadFile(directoryName, fileName, filePath);
			} else {
				if(fileName.endsWith(UPDATE_SUFIX)) {
					/* Update */
					String fileNameToUpdate = fileName.substring(0, fileName.length() - UPDATE_SUFIX.length());
					s3Client.deleteFile(directoryName, fileNameToUpdate);
					s3Client.uploadFile(directoryName, fileNameToUpdate, filePath);
					(new File(filePath)).renameTo(new File(localDirName + File.separator + fileNameToUpdate));
					noDownload.add(fileNameToUpdate);					
				} else if(fileName.endsWith(DELETE_SUFIX)) {
					/* Delete */					
					String fileNameToDelete = fileName.substring(0, fileName.length() - DELETE_SUFIX.length());
					s3Client.deleteFile(directoryName, fileNameToDelete);
					(new File(filePath)).delete();
					noDownload.add(fileNameToDelete);					
				}
			}
		}
		remoteFileNames.removeAll(noDownload);

		/* Download */
		for (String fileName : remoteFileNames) {
			String filePath = localDirName + File.separator + fileName;
			File file = new File(filePath);
			if (!file.exists()) {
				s3Client.downloadFile(directoryName, fileName, filePath);
			}
		}
		
		Log.info("[Finish]");
	}

}
