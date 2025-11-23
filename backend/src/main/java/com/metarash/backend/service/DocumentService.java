package com.metarash.backend.service;

import com.metarash.backend.exception.EntityNotFoundException;
import com.metarash.backend.mapper.DocumentMapper;
import com.metarash.backend.model.dto.request.DocumentCreateDto;
import com.metarash.backend.model.dto.request.DocumentUpdateDto;
import com.metarash.backend.model.entity.Category;
import com.metarash.backend.model.entity.Document;
import com.metarash.backend.model.entity.DocumentStatus;
import com.metarash.backend.model.entity.User;
import com.metarash.backend.repository.CategoryRepository;
import com.metarash.backend.repository.DocumentRepository;
import com.metarash.backend.validation.DocumentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final MinioService minioService;
    private final DocumentMapper documentMapper;
    private final DocumentValidator documentValidator;

    @Transactional
    public Document createDocument(DocumentCreateDto dto, MultipartFile file, User currentUser) {
        documentValidator.validateCreate(dto, file, currentUser);
        Category category = Optional.ofNullable(dto.categoryId())
                .map(this::getCategory)
                .orElse(null);

        String fileName;
        try {
            fileName = minioService.createFile(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }

        Document document = documentMapper.toEntity(dto);
        document.setCategory(category);
        document.setAuthor(currentUser);
        document.setFilePath(fileName);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        return documentRepository.save(document);
    }

    public Page<Document> getDocumentsByStatus(DocumentStatus status, Pageable pageable) {
        return documentRepository.findByStatus(status, pageable);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
    }

    @Transactional
    public Document updateDocument(Long id, DocumentUpdateDto dto, MultipartFile file, User currentUser) {
        Document document = getDocumentById(id);
        documentValidator.validateUpdate(document, file, currentUser);
        documentMapper.updateFromDto(dto, document);

        if (dto.categoryId() != null) {
            document.setCategory(getCategory(dto.categoryId()));
        }

        if (file != null && !file.isEmpty()) {
            try {
                minioService.updateFile(file, document.getFilePath());
                document.setFileType(file.getContentType());
                document.setFileSize(file.getSize());
            } catch (Exception e) {
                throw new RuntimeException("Failed to update file in MinIO", e);
            }
        }

        return documentRepository.save(document);
    }

    @Transactional
    public void archiveDocument(Long id, User currentUser) {
        Document document = getDocumentById(id);
        documentValidator.validateUpdate(document, null, currentUser);  // No file for archive
        document.archive();

        try {
            minioService.deleteFile(document.getFilePath());
            document.setFilePath(null);
            document.setFileType(null);
            document.setFileSize(null);
        } catch (Exception e) {
            log.warn("Failed to delete file from MinIO during archive: {}", e.getMessage());
        }

        documentRepository.save(document);
    }

    public InputStream getDocumentFile(Long id) {
        Document document = getDocumentById(id);
        try {
            return minioService.readFile(document.getFilePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file from MinIO", e);
        }
    }

    public String getDocumentFileUrl(Long id) {
        Document document = getDocumentById(id);
        try {
            return minioService.getFileUrl(document.getFilePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file URL from MinIO", e);
        }
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }
}