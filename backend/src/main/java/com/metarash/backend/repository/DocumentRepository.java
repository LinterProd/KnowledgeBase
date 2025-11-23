package com.metarash.backend.repository;

import com.metarash.backend.model.entity.Category;
import com.metarash.backend.model.entity.Document;
import com.metarash.backend.model.entity.DocumentStatus;
import com.metarash.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Базовые фильтры
    List<Document> findByStatusOrderByCreatedAtDesc(String status);
    List<Document> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
    List<Document> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    // Поиск
    List<Document> findByTitleContainingIgnoreCase(String title);
    // Для пагинации (опционально)
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    List<Document> findByAuthor(User author);
    List<Document> findByCategory(Category category);
    long countByAuthor(User author);
}
