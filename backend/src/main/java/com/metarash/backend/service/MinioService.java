package com.metarash.backend.service;

import com.metarash.backend.config.props.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String createFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        log.info("Creating Minio File for respons as filename: {}", fileName);
        putObject(file, fileName);
        return fileName;
    }

    public void updateFile(MultipartFile file, String existingFileName) throws Exception {
        putObject(file, existingFileName);
    }

    private void putObject(MultipartFile file, String fileName) throws Exception {
        log.info("Putting Minio File for respons as filename: {}", fileName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.bucketName())
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    public InputStream readFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucketName())
                        .object(fileName)
                        .build()
        );
    }

    public String getFileUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioProperties.bucketName())
                        .object(fileName)
                        .expiry(60 * 60 * 24) // 1 день
                        .build()
        );
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.bucketName())
                        .object(fileName)
                        .build()
        );
    }
}