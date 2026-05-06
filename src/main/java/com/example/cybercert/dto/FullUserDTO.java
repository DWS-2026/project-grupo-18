package com.example.cybercert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FullUserDTO(
    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY) Long id,
    @Schema(example = "juan") String username,
    @Schema(example = "juan@mail.com") String email,
    @Schema(example = "secret123") String password,
    @Schema(example = "10", accessMode = Schema.AccessMode.READ_ONLY) Long profileImageId
    ) {
}

