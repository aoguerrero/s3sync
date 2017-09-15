package io.aoguerrero.objstoragesync.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.aoguerrero.objstoragesync.Config;
import io.aoguerrero.objstoragesync.ObjStorageClient;
import io.aoguerrero.objstoragesync.Sync;

public class S3Sync extends Sync<AmazonS3> {

	@Override
	public void start(String localDirName) throws Exception {
		BasicAWSCredentials credentials = new BasicAWSCredentials(Config.getInstance().getValue("accessKey"),
				Config.getInstance().getValue("secretKey"));
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Config.getInstance().getValue("region")).build();
		ObjStorageClient<AmazonS3> s3Client = new S3Client(amazonS3);
		this.sync(localDirName, s3Client);
	}
}
