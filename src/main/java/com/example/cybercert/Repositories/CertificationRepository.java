package com.example.cybercert.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cybercert.Models.Certification;


public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findByName(String name);

}
