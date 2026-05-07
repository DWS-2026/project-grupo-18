package com.example.cybercert.controllers.rest;

import com.example.cybercert.dto.CertificationMapper;
import com.example.cybercert.dto.ImageDTO;
import com.example.cybercert.models.Certification;
import com.example.cybercert.services.CertificationService;
import com.example.cybercert.dto.CertificationDTO;

import com.example.cybercert.models.Image;
import com.example.cybercert.services.ImageService;
import com.example.cybercert.dto.ImageMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.cybercert.services.CertificationDocumentService;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationRestController {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private CertificationMapper certificationMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private CertificationDocumentService certificationDocumentService;

    public CertificationRestController(CertificationService certificationService,
            CertificationMapper certificationMapper) {
        this.certificationService = certificationService;
        this.certificationMapper = certificationMapper;
    }

    @GetMapping()
    public ResponseEntity<Page<CertificationDTO>> getAllCertifications(Pageable pageable) {
        Page<Certification> certifications = certificationService.findAll(pageable);
        Page<CertificationDTO> dtoPage = certifications.map(certificationMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationDTO> getCertificationById(@PathVariable Long id) {
        return certificationService.findById(id)
                .map(certificationMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<CertificationDTO> createCertification(@RequestBody CertificationDTO certificationDTO) {

        Certification certification = certificationMapper.toDomain(certificationDTO);
        certification = certificationService.createCertification(certification);
        certificationDTO = certificationMapper.toDTO(certification);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(certificationDTO.id()).toUri();

        return ResponseEntity.created(location).body(certificationDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationDTO> updateCertification(@PathVariable Long id,
            @RequestBody CertificationDTO certificationDTO) {
        Optional<Certification> existingCertification = certificationService.findById(id);
        if (existingCertification.isPresent()) {
            Certification updatedCertification = certificationMapper.toDomain(certificationDTO);
            updatedCertification = certificationService.updateCertification(id, updatedCertification);
            return ResponseEntity.ok(certificationMapper.toDTO(updatedCertification));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CertificationDTO> deleteCertification(@PathVariable Long id) {
        Optional<Certification> existingCertification = certificationService.findById(id);
        if (existingCertification.isPresent()) {
            certificationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDTO> createCertificationImage(@PathVariable Long id, MultipartFile imageFile)
            throws IOException {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Image image = imageService.createImage(imageFile.getInputStream());
        certificationService.addImageToCertification(id, image);

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<ImageDTO> deleteCertificationImage(@PathVariable Long id) throws IOException {
        Certification certification = certificationService.findById(id).orElseThrow();
        Image image = certification.getImage();
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        certificationService.removeImageFromCertification(id);
        imageService.deleteImage(image.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> getCertificationDocument(@PathVariable Long id) throws MalformedURLException {
        Certification certification = certificationService.findById(id).orElseThrow();
        if (certification.getDocumentPath() == null) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = Paths.get(certification.getDocumentPath()).normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                filePath.getFileName().toString() + "\"")
                .body(resource);
    }

    @PostMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCertificationDocument(@PathVariable Long id, MultipartFile documentFile)
            throws IOException {
        if (documentFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        try {
            certificationDocumentService.store(documentFile, id);
            return ResponseEntity.ok("Document uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error storing document");
        }
    }
}
