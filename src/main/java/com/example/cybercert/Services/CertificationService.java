package com.example.cybercert.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Repositories.CertificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    public Optional<Certification> findById(Long id) {
        return certificationRepository.findById(id);
    }

    public List<Certification> findAll() {
        return certificationRepository.findAll();
    }

    public Certification save(Certification certification) {
        return certificationRepository.save(certification);
    }

    public void deleteById(Long id) {
        certificationRepository.deleteById(id);
    }
}
   