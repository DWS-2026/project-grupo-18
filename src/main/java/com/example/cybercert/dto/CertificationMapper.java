package com.example.cybercert.dto;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cybercert.Models.Certification;

@Mapper(componentModel = "spring", uses = { ImageMapper.class })
public interface CertificationMapper {

    CertificationDTO toDTO(Certification certification);

    List<CertificationDTO> toDTOs(Collection<Certification> certifications);

    @Mapping(target = "comments", ignore = true)
    Certification toDomain(CertificationDTO certificationDTO);

}