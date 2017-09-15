package io.aoguerrero.objstoragesync;

import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Base64;
import java.util.List;

public abstract class ObjStorageClient<T> {

	protected Decoder decoder;

	protected Encoder encoder;

	protected T connection;

	public ObjStorageClient(T connection) {
		this.connection = connection;
		this.encoder = Base64.getEncoder();
		this.decoder = Base64.getDecoder();
	}

	protected String getKey(String fileName) throws Exception {
		return encoder.encodeToString(fileName.getBytes("UTF-8"));
	}

	protected String getFileName(String key) throws Exception {
		return new String(decoder.decode(key), "UTF-8");
	}

	public abstract List<String> listFiles(String directoryName) throws Exception;

	public abstract void uploadFile(String directoryName, String fileName, String filePath) throws Exception;

	public abstract void downloadFile(String directoryName, String fileName, String filePath) throws Exception;

	public abstract void deleteFile(String directoryName, String fileName) throws Exception;

}
