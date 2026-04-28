package com.example.cybercert.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CommentDTO (
        Long id,
        @JsonAlias({"text"})
        String content,
        String authorUsername,
        Long certificationId,
        int rating) {
    
}
