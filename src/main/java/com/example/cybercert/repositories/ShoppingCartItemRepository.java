package com.example.cybercert.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cybercert.models.ShoppingCartItem;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    List<ShoppingCartItem> findByUserId(Long userId);

    boolean existsByUserIdAndCertificationId(Long userId, Long certificationId);

    void deleteByUserIdAndCertificationId(Long userId, Long certificationId);

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);
    
}
