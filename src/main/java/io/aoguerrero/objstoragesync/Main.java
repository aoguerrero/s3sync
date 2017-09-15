package io.aoguerrero.objstoragesync;

import com.amazonaws.services.s3.AmazonS3;

import io.aoguerrero.objstoragesync.s3.S3Sync;

public class Main {

	public static void main(String[] args) throws Exception {
		Sync<AmazonS3> sync = new S3Sync();
		sync.start(System.getProperty("user.dir"));
	}
}
