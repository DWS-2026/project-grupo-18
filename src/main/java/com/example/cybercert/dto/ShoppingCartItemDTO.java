package com.example.cybercert.dto;

public record ShoppingCartItemDTO(
    Long id,
    Long userId,
    Long certificationId,
    int quantity
) {
}