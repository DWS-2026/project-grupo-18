package com.example.cybercert.Controllers.rest;

import com.example.cybercert.dto.CertificationMapper;
import com.example.cybercert.dto.CertificationDTO;
import com.example.cybercert.Models.Certification;
import com.example.cybercert.Services.CertificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public ResponseEntity<List<CertificationDTO>> getAllCertifications() {
        List<Certification> certifications = certificationService.findAll();
        return ResponseEntity.ok(certificationMapper.toDTOs(certifications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationDTO> getCertificationById(@PathVariable Long id) {
        return certificationService.findById(id)
                .map(certificationMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CertificationDTO> createCertification(@RequestBody CertificationDTO certificationDTO) {

        Certification certification = certificationMapper.toDomain(certificationDTO);
        certification = certificationService.createCertification(certification);
        certificationDTO = certificationMapper.toDTO(certification);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(certificationDTO.id()).toUri();

        return ResponseEntity.created(location).body(certificationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CertificationDTO> deleteCertification(@PathVariable Long id) {
        Optional<Certification> certification = certificationService.findById(id);
        if (!certification.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        certificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}