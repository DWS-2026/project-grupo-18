package com.example.cybercert.Controllers.rest;

import com.example.cybercert.dto.CommentMapper;
import com.example.cybercert.dto.CommentDTO;
import com.example.cybercert.Models.Comment;
import com.example.cybercert.Services.CommentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentRestController(CommentService commentService,
            CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        List<Comment> comments = commentService.findAll();
        return ResponseEntity.ok(commentMapper.toDTOs(comments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(commentMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
