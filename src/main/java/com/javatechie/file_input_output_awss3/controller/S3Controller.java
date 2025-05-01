package com.javatechie.file_input_output_awss3.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Client s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    public S3Controller(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // Upload Endpoint
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String keyName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return ResponseEntity.ok("File uploaded: " + keyName);
    }

    // Download Endpoint
    @GetMapping("/download/{keyName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        ResponseInputStream<GetObjectResponse> objectData = s3Client.getObject(getObjectRequest);

        String encodedFileName = URLEncoder.encode(keyName, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(objectData));
    }

    // Delete Endpoint
    @DeleteMapping("/delete/{keyName}")
    public ResponseEntity<String> deleteFile(@PathVariable String keyName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return ResponseEntity.ok("File deleted: " + keyName);
    }
}

