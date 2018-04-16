package io.aoguerrero.objstoragesync.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;

import io.aoguerrero.objstoragesync.Config;
import io.aoguerrero.objstoragesync.Log;
import io.aoguerrero.objstoragesync.ObjStorageClient;

public class S3Client extends ObjStorageClient<AmazonS3> {

	public S3Client(AmazonS3 amazonS3) {
		super(amazonS3);
	}

	@Override
	public List<String> listFiles(String directoryName) throws Exception {
		ObjectListing objectListing = connection.listObjects(directoryName);
		List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
		List<String> result = new ArrayList<String>();
		for (S3ObjectSummary objectSummary : objectSummaries) {
			String key = objectSummary.getKey();
			if(!key.contains("/")) { /* No se incluyen archivos en directorios */
				String fileName = getFileName(key);
				result.add(fileName);
				Log.info("[S3 file] " + fileName);
			}
		}
		return result;
	}

	@Override
	public void uploadFile(String directoryName, String fileName, String filePath) throws Exception {
		String key = getKey(fileName);
		List<PartETag> partETags = new ArrayList<PartETag>();
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(directoryName, key);
		InitiateMultipartUploadResult initResponse = connection.initiateMultipartUpload(initRequest);
		File file = new File(filePath);
		long contentLength = file.length();
		if (contentLength == 0) {
			Log.info("[Empty file] " + fileName);
			return;
		}
		long partSize = 5242880;
		long filePosition = 0;

		for (int i = 1; filePosition < contentLength; i++) {
			partSize = Math.min(partSize, (contentLength - filePosition));
			UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(directoryName).withKey(key)
					.withUploadId(initResponse.getUploadId()).withPartNumber(i).withFileOffset(filePosition)
					.withFile(file).withPartSize(partSize);
			partETags.add(connection.uploadPart(uploadRequest).getPartETag());
			filePosition += partSize;
		}
		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(directoryName, key,
				initResponse.getUploadId(), partETags);

		connection.completeMultipartUpload(compRequest);
		Log.info("[Upload] " + fileName);
		
		boolean removeFiles = Boolean.valueOf(Config.getConfig().getValue("removeFiles"));
		if(removeFiles) {
			Log.info("[Removed] " + fileName);
			file.delete();
		}
	}

	@Override
	public void downloadFile(String directoryName, String fileName, String filePath) throws Exception {
		String key = getKey(fileName);
		S3Object s3Object = connection.getObject(directoryName, key);
		S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
		FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
		byte[] buffer = new byte[1024];
		int readLength = 0;
		while ((readLength = s3ObjectInputStream.read(buffer)) > 0) {
			fileOutputStream.write(buffer, 0, readLength);
		}
		s3ObjectInputStream.close();
		fileOutputStream.close();
		Log.info("[Download] " + fileName);
	}

	@Override
	public void deleteFile(String directoryName, String fileName) throws Exception {
		String key = getKey(fileName);
		connection.deleteObject(directoryName, key);
		Log.info("[Delete] " + fileName);
	}

}
