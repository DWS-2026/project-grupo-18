package com.example.cybercert.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cybercert.Models.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCertificationIdOrderByCreatedAtDesc(Long certificationId);
}
