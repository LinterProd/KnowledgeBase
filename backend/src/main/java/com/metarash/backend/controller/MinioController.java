package com.metarash.backend.controller;

import com.metarash.backend.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/minio")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam("file") MultipartFile file) {
        log.info("Creating Minio File");
        try {
            log.info("Creating Minio File for respons as filename: {}", file.getOriginalFilename());
            String fileName = minioService.createFile(file);
            log.info("Created Minio File for respons as filename: {}", fileName);
            return ResponseEntity.ok("Создан: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update/{fileName}")
    public ResponseEntity<String> update(@RequestParam("file") MultipartFile file, @PathVariable String fileName) {
        try {
            minioService.updateFile(file, fileName);
            return ResponseEntity.ok("Обновлён: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/read/{fileName}")
    public ResponseEntity<byte[]> read(@PathVariable String fileName) {
        try (InputStream is = minioService.readFile(fileName)) {
            byte[] bytes = is.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/url/{fileName}")
    public ResponseEntity<String> getUrl(@PathVariable String fileName) {
        try {
            return ResponseEntity.ok(minioService.getFileUrl(fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> delete(@PathVariable String fileName) {
        try {
            minioService.deleteFile(fileName);
            return ResponseEntity.ok("Удалён: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}