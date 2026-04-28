package com.example.cybercert.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.Image;
import com.example.cybercert.Repositories.CertificationRepository;
import com.example.cybercert.Repositories.ImageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Optional<Certification> findById(Long id) {
        return certificationRepository.findById(id);
    }

    public List<Certification> findAll() {
        return certificationRepository.findAll();
    }

    public Certification save(Certification certification) {
        return certificationRepository.save(certification);
    }

    @Transactional
    public void deleteById(Long id) {
        certificationRepository.deleteById(id);
    }

    public Certification createCertification(Certification certification) {
        if (certification.getId() != null) {
            throw new IllegalArgumentException();
        }

        return certificationRepository.save(certification);

    }

    public int count() {
        return (int) certificationRepository.count();
    }
}
