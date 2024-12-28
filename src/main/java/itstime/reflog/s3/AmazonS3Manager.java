package itstime.reflog.s3;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import itstime.reflog.config.AmazonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {
	private final AmazonS3 amazonS3;

	private final AmazonConfig amazonConfig;

	public String uploadFile(String keyName, MultipartFile file){
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());
		try{
			amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(),keyName,file.getInputStream(), metadata));
		}catch (IOException e){
			log.error("error at AmazonS3Manager uploadFile : {}",(Object) e.getStackTrace());
		}
		return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
	}

	public void deleteImage(String fileUrl){
		try{
			String splitStr = ".com/";
			String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());
			amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), fileName));
		}
		catch(SdkClientException e){
			log.error("Error deleting file from s3");
		}
	}
}
