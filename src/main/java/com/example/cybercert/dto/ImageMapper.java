package com.example.cybercert.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cybercert.Models.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);

    @Mapping(target = "imageFile", ignore = true)
    Image toDomain(ImageDTO imageDTO);
}
