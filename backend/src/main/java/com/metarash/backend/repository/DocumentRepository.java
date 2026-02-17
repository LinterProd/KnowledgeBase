package com.metarash.backend.repository;

import com.metarash.backend.model.entity.Document;
import com.metarash.backend.model.entity.DocumentStatus;
import com.metarash.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Базовый фильтр
    List<Document> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    // Поиск
    List<Document> findByTitleContainingIgnoreCase(String title);
    List<Document> findByStatus(DocumentStatus status);
    List<Document> findByAuthor(User author);

    long countByAuthor(User author);
}
