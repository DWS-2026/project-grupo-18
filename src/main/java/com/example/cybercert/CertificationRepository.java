package com.example.cybercert;

import com.example.cybercert.Certification;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findByName(String name);

}
