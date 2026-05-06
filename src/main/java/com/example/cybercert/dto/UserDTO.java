package com.example.cybercert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDTO(
        @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY) Long id,
        @Schema(example = "juan") String username,
        @Schema(example = "juan@mail.com") String email,
        @Schema(example = "10", accessMode = Schema.AccessMode.READ_ONLY) Long profileImageId) {
}

