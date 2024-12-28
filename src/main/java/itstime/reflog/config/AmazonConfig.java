package itstime.reflog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Configuration
@Getter
public class AmazonConfig {
	private AWSCredentials awsCredentials;

	@Value("${cloud.aws.credentials.accessKey}")
	private String accesskey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretkey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;


	@PostConstruct
	public void init(){ this.awsCredentials = new BasicAWSCredentials(accesskey, secretkey); }

	@Bean
	public AmazonS3 s3Client(){
		AWSCredentials awsCredentials1 = new BasicAWSCredentials(accesskey, secretkey);
		return AmazonS3ClientBuilder.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(awsCredentials1))
			.build();
	}

	@Bean
	public AWSCredentialsProvider awsCredentialsProvider(){ return new AWSStaticCredentialsProvider(awsCredentials); }
}
