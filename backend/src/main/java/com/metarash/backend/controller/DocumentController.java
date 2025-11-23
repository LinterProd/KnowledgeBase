package com.metarash.backend.controller;

import com.metarash.backend.model.dto.request.DocumentCreateDto;
import com.metarash.backend.model.dto.request.DocumentUpdateDto;
import com.metarash.backend.model.dto.response.DocumentResponseDto;
import com.metarash.backend.model.entity.Document;
import com.metarash.backend.model.entity.DocumentStatus;
import com.metarash.backend.model.entity.User;
import com.metarash.backend.service.DocumentService;
import com.metarash.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DocumentResponseDto> createDocument(
            @RequestPart("dto") @Valid DocumentCreateDto dto,
            @RequestPart("file") MultipartFile file) {
        User currentUser = getCurrentUser();
        Document document = documentService.createDocument(dto, file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(document));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponseDto>> getDocumentsByStatus(
            @RequestParam(defaultValue = "PUBLISHED") DocumentStatus status,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Document> documents = documentService.getDocumentsByStatus(status, pageable);
        return ResponseEntity.ok(documents.map(this::mapToDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return ResponseEntity.ok(mapToDto(document));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DocumentResponseDto> updateDocument(
            @PathVariable Long id,
            @RequestPart("dto") @Valid DocumentUpdateDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {  // Файл опциональный
        User currentUser = getCurrentUser();
        Document updated = documentService.updateDocument(id, dto, file, currentUser);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try (InputStream is = documentService.getDocumentFile(id)) {
            byte[] bytes = is.readAllBytes();
            Document document = documentService.getDocumentById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getFileType()));
            headers.setContentDisposition(ContentDisposition.attachment().filename(document.getFilePath()).build());
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> archiveDocument(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        documentService.archiveDocument(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/url")
    public ResponseEntity<String> getDocumentUrl(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(documentService.getDocumentFileUrl(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Получение текущего пользователя
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current User: {}", auth.getName());
        if (!auth.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        String username = auth.getName();
        return userService.getUserByUsername(username);
    }

    private DocumentResponseDto mapToDto(Document document) {
        return new DocumentResponseDto(document.getId(), document.getTitle(), document.getDescription(),
                document.getFilePath(), document.getFileType(), document.getFileSize(),
                document.getCategory() != null ? document.getCategory().getId() : null,
                document.getAuthor().getId(), document.getStatus());
    }
}