package com.example.cybercert.Controllers.rest;

import java.io.IOException;
import java.sql.SQLException;

import javax.print.attribute.standard.Media;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cybercert.Models.Image;
import com.example.cybercert.Services.ImageService;
import com.example.cybercert.dto.ImageDTO;
import com.example.cybercert.dto.ImageMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    ImageService imageService;

    @Autowired
    ImageMapper imageMapper;

    @GetMapping("/{id}")
    public ImageDTO getImage(@PathVariable Long id) {
        return imageMapper.toDTO(imageService.getImage(id));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<Object> getImageFile(@PathVariable Long id) throws SQLException, IOException {
        Resource imageFile = imageService.getImageFile(id);

        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

}
