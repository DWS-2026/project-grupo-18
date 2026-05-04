package com.example.cybercert.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cybercert.models.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCertificationIdOrderByCreatedAtDesc(Long certificationId);
    Page<Comment> findByCertificationIdOrderByCreatedAtDesc(Long certificationId, Pageable pageable);
}
