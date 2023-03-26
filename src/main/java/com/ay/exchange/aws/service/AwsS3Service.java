package com.ay.exchange.aws.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.ay.exchange.aws.exception.EmptyFileException;
import com.ay.exchange.aws.exception.FileUploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;
    private final String FILE_EXTENSION_SEPARATOR = ".";
    private final int UPLOAD_FILE = 0;
    private final int UPDATE_PROFILE = 1;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void uploadFile(MultipartFile multipartFile, String filePath) {
        validateFileExists(multipartFile);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, filePath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileUploadFailedException();
        }
        //System.out.println("amazon Url: " + amazonS3Client.getUrl(bucketName, fileName).toString());
        // https://exchange-data-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile
    }

    public ByteArrayResource downloadFile(String filePath) {
        validateFileExistsAtUrl(filePath);

        S3Object s3Object = amazonS3Client.getObject(bucketName, filePath);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return new ByteArrayResource(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProfile(String path) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, path));
    }

    public void deleteUserFiles(String userId) {
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName, userId);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        String[] keys = new String[objectSummaries.size()];
        int cnt = 0;
        for (S3ObjectSummary summary : objectSummaries) {
            keys[cnt++] = summary.getKey();
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
        amazonS3Client.deleteObjects(deleteObjectsRequest);
    }

    public void deleteUserFile(String filePath) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, filePath));
    }

    private void validateFileExistsAtUrl(String filePath) {
        if (!amazonS3Client.doesObjectExist(bucketName, filePath)) {
            throw new EmptyFileException();
        }
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty())
            throw new EmptyFileException();
    }

    public String buildFileName(String originalFileName, String userId, int type) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();
        if (type == UPLOAD_FILE) { //0이면 자료 1이면 프로필
            sb.append(userId);
            sb.append("/");
        }
        sb.append(fileName);
        sb.append("_");
        sb.append(now);
        sb.append(fileExtension);
        return sb.toString();
    }

}
