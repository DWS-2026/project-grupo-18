package com.example.cybercert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "juan") String username,
        @Schema(example = "juan@mail.com") String email) {
}

