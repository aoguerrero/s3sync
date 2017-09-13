package io.aoguerrero.s3client;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		
		/* Carga de archivo de propiedades */
		InputStream input = Main.class.getClassLoader().getResourceAsStream("main.properties");	
		Properties props = new Properties();
		props.load(input);
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("???", "???");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
		                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
		                        .withRegion("us-east-1")
		                        .build();
		
		List<Bucket> buckets = s3Client.listBuckets();
		logger.info("Your Amazon S3 buckets are:");
		for (Bucket b : buckets) {
			logger.info("* " + b.getName());
		}
		
	}

}
