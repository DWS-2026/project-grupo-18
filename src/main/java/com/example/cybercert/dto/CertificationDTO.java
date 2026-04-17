package com.example.cybercert.dto;

import java.util.List;

public record CertificationDTO(
        Long id,
        String name,
        String description,
        int duration,
        String format,
        String language,
        String level,
        List<String> requirements,
        List<String> contents) {
}
