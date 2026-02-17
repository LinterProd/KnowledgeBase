package com.metarash.backend.validation;

import com.metarash.backend.model.entity.Document;
import com.metarash.backend.model.entity.User;
import jakarta.validation.ValidationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class DocumentValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;  // 10MB, configure via properties
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "text/plain", "text/csv"
    );

    public void validateCreate(MultipartFile file, User currentUser) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds limit: " + MAX_FILE_SIZE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new ValidationException("Invalid file type: " + contentType);
        }
        if (currentUser == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
    }

    public void validateUpdate(Document document, MultipartFile file, User currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        if (!document.getAuthor().equals(currentUser) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Only author or admin can update");
        }
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ValidationException("File size exceeds limit: " + MAX_FILE_SIZE);
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
                throw new ValidationException("Invalid file type: " + contentType);
            }
        }
    }
}