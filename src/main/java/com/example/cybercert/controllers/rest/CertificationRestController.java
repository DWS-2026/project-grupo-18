package com.example.cybercert.controllers.rest;

import com.example.cybercert.dto.CertificationMapper;
import com.example.cybercert.models.Certification;
import com.example.cybercert.services.CertificationService;
import com.example.cybercert.dto.CertificationDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationRestController {

    private final CertificationService certificationService;
    private final CertificationMapper certificationMapper;

    public CertificationRestController(CertificationService certificationService,
            CertificationMapper certificationMapper) {
        this.certificationService = certificationService;
        this.certificationMapper = certificationMapper;
    }

    @GetMapping("/")
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

    @PostMapping("/")
    public ResponseEntity<CertificationDTO> createCertification(@RequestBody CertificationDTO certificationDTO) {

        Certification certification = certificationMapper.toDomain(certificationDTO);
        certification = certificationService.createCertification(certification);
        certificationDTO = certificationMapper.toDTO(certification);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(certificationDTO.id()).toUri();

        return ResponseEntity.created(location).body(certificationDTO);
    }

}