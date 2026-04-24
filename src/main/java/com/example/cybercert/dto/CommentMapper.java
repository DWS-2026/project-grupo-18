package com.example.cybercert.dto;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cybercert.Models.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "text", target = "content")
    @Mapping(source = "user.username", target = "authorUsername")
    @Mapping(source = "certification.id", target = "certificationId")
    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOs(Collection<Comment> comments);

}