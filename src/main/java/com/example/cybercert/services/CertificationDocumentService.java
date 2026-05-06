package com.example.cybercert.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.example.cybercert.models.Certification;
import com.example.cybercert.repositories.CertificationRepository;

@Service
public class CertificationDocumentService {

    @Autowired
    private CertificationRepository certificationRepository;

    public final Path documentStorageLocation = Paths.get("./uploads/").normalize();

    public CertificationDocumentService() {
        try {
            Files.createDirectories(documentStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public void store(MultipartFile file, Long certID) throws IOException {

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalName == null || originalName.contains("..")) {
            throw new RuntimeException("Invalid file name");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Content type not allowed");
        }

        Path target = documentStorageLocation.resolve(originalName);

        if (!target.startsWith(documentStorageLocation)) {
            throw new SecurityException("Path traversal detected");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        Certification cert = certificationRepository.findById(certID)
                .orElseThrow(() -> new RuntimeException("Certification not found with id: " + certID));

        cert.setDocumentPath(target.toString());
        certificationRepository.save(cert);

    }
}
