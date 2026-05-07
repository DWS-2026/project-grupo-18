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

    public final Path documentStorageLocation = Paths.get("./uploads/").toAbsolutePath().normalize();

    public CertificationDocumentService() {
        try {
            Files.createDirectories(documentStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null) {
            throw new RuntimeException("Invalid file name");
        }

        String cleaned = StringUtils.cleanPath(originalFilename);

        if (cleaned.contains("..") || cleaned.startsWith("/") || cleaned.startsWith("\\") || Paths.get(cleaned).isAbsolute()) {
            throw new RuntimeException("Invalid file name");
        }

        return cleaned;
    }

    public Path getDocumentPath(String documentPath) {
        Path filePath = Paths.get(documentPath);
        if (!filePath.isAbsolute()) {
            filePath = documentStorageLocation.resolve(filePath);
        }
        filePath = filePath.normalize();

        if (!filePath.startsWith(documentStorageLocation)) {
            throw new SecurityException("Path traversal detected");
        }

        return filePath;
    }

    public Resource loadDocumentAsResource(String documentPath) throws MalformedURLException {
        Path filePath = getDocumentPath(documentPath);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }
        return resource;
    }

    public void store(MultipartFile file, Long certID) throws IOException {

        String originalName = sanitizeFilename(file.getOriginalFilename());

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Content type not allowed");
        }

        Path target = documentStorageLocation.resolve(originalName).normalize();

        if (!target.startsWith(documentStorageLocation)) {
            throw new SecurityException("Path traversal detected");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        Certification cert = certificationRepository.findById(certID)
                .orElseThrow(() -> new RuntimeException("Certification not found with id: " + certID));

        cert.setDocumentPath(documentStorageLocation.relativize(target).toString());
        certificationRepository.save(cert);

    }
}
