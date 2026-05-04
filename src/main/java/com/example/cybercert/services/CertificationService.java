package com.example.cybercert.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cybercert.models.Certification;
import com.example.cybercert.models.Image;
import com.example.cybercert.repositories.CertificationRepository;
import com.example.cybercert.repositories.ImageRepository;

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

    public Page<Certification> findAll(Pageable pageable) {
        return certificationRepository.findAll(pageable);
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
            throw new IllegalArgumentException("Certification ID must be null for creation");
        }

        if (certification.getImage() != null) {

            Long imageId = certification.getImage().getId();

            if (imageId != null) {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

                certification.setImage(image);
            } else {
                certification.setImage(null);
            }
        }

        return certificationRepository.save(certification);
    }

    @Transactional
    public Certification updateCertification(Long id, Certification updatedCertification) {
        Certification existingCertification = certificationRepository.findById(id)
                .orElseThrow();

        updatedCertification.setId(id);
        if (existingCertification.getImage() != null) {
            updatedCertification.setImage(existingCertification.getImage());
        }

        if (updatedCertification.getComments() == null) {
            updatedCertification.setComments(existingCertification.getComments());
        } else {
            updatedCertification.getComments().forEach(comment -> comment.setCertification(updatedCertification));
        }

        certificationRepository.save(updatedCertification);

        return updatedCertification;
    }

    public Certification addImageToCertification(long id, Image image) {
        Certification certification = certificationRepository.findById(id).orElseThrow();
        certification.setImage(image);
        certificationRepository.save(certification);

        return certification;
    }

    public Certification removeImageFromCertification(long id) {
        Certification certification = certificationRepository.findById(id).orElseThrow();
        certification.setImage(null);
        certificationRepository.save(certification);

        return certification;
    }

    public int count() {
        return (int) certificationRepository.count();
    }
}
