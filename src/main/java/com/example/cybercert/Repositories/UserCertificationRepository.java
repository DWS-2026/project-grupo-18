package com.example.cybercert.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cybercert.Models.UserCertification;

public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {

    boolean existsByUserIdAndCertificationId(Long userId, Long certificationId);

    List<UserCertification> findByUserId(Long userId);

    List<UserCertification> findByUserIdOrderByPurchasedAtDesc(Long userId);
}
