package com.example.cybercert.dto;

import org.mapstruct.Mapper;

import com.example.cybercert.Models.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}
