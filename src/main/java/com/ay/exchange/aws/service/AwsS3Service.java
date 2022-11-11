package com.ay.exchange.aws.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.ay.exchange.aws.exception.EmptyFileException;
import com.ay.exchange.aws.exception.FileUploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;
    private final String FILE_EXTENSION_SEPARATOR=".";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, String userId, int type){
        validateFileExists(multipartFile);

        String fileName=buildFileName(multipartFile.getOriginalFilename(), userId, type);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream=multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucketName,fileName,inputStream,objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch(IOException e){
            throw new FileUploadFailedException();
        }
        System.out.println("amazon Url: "+amazonS3Client.getUrl(bucketName,fileName).toString());
        return fileName;
        //return amazonS3Client.getUrl(bucketName,fileName).toString();
    }

    public byte[] downloadFile(String filePath) {
        validateFileExistsAtUrl(filePath);

        S3Object s3Object=amazonS3Client.getObject(bucketName,filePath);
        S3ObjectInputStream inputStream=s3Object.getObjectContent();

        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void validateFileExistsAtUrl(String filePath) {
        if(!amazonS3Client.doesObjectExist(bucketName,filePath)) {
            //throw new FileNotFoundException();
            throw new RuntimeException();
        }
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if(multipartFile.isEmpty())
            throw new EmptyFileException();
    }

    private String buildFileName(String originalFileName, String userId, int type){
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension=originalFileName.substring(fileExtensionIndex);
        String fileName=originalFileName.substring(0,fileExtensionIndex);
        String now=String.valueOf(System.currentTimeMillis());

        StringBuilder sb=new StringBuilder();
        if(type==0){
            sb.append(userId);
        }else{
            sb.append("profile");
        }
        sb.append("/");
        sb.append(fileName);
        sb.append("_");
        sb.append(now);
        sb.append(fileExtension);
        return sb.toString();
    }

    public static ContentDisposition createContentDisposition(String filePath){
        String fileName=filePath.substring(
                filePath.lastIndexOf("/")+1);
        System.out.println("fileName: "+fileName);

        return ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
    }

}
