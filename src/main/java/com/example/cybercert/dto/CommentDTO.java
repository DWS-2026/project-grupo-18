package com.example.cybercert.dto;

public record CommentDTO (
        Long id,
        String content,
        String authorUsername,
        Long certificationId) {
    
}
