package io.aoguerrero.s3client;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class S3Client extends ObjectStorageClient<AmazonS3> {

	private static final Logger logger = LoggerFactory.getLogger(S3Client.class);

	private Decoder decoder;
	private Encoder encoder;

	public S3Client(AmazonS3 amazonS3) {
		super(amazonS3);
		this.encoder = Base64.getEncoder();
		this.decoder = Base64.getDecoder();
	}

	@Override
	public List<String> listFiles(String directoryName) throws Exception {
		ObjectListing objectListing = connection.listObjects(directoryName);
		List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
		List<String> result = new ArrayList<String>();
		for (S3ObjectSummary objectSummary : objectSummaries) {
			String key = objectSummary.getKey();
			String fileName = getFileName(key);
			result.add(fileName);
			logger.info("File on S3 " + directoryName + "/" + key + "(" + fileName + ")");
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
		if(contentLength == 0)
			return;
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
		logger.info("upload " + filePath + "->" + directoryName + "/" + key + "(" + fileName + ")");
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
		logger.info("download " + directoryName + "/" + key + "(" + fileName + ") ->" + filePath);
	}

	@Override
	public void deleteFile(String directoryName, String fileName) throws Exception {
		String key = getKey(fileName);
		connection.deleteObject(directoryName, key);
		logger.info("delete " + directoryName + "/" + key + "(" + fileName + ")");
	}

	/* ***** */

	private String getKey(String fileName) throws Exception {
		return encoder.encodeToString(fileName.getBytes("UTF-8"));
	}

	private String getFileName(String key) throws Exception {
		return new String(decoder.decode(key), "UTF-8");

	}
}
