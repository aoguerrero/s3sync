package io.aoguerrero.s3client;

import java.util.List;

public abstract class ObjectStorageClient<T> {
	
	protected T connection;
	
	public ObjectStorageClient(T connection){
		this.connection = connection;
	}
	
	public abstract List<String> listFiles(String directoryName) throws Exception;
	
	public abstract void uploadFile(String directoryName, String fileName, String filePath) throws Exception;
	
	public abstract void downloadFile(String directoryName, String fileName, String filePath) throws Exception;
	
	public abstract void deleteFile(String directoryName, String fileName) throws Exception;

}
