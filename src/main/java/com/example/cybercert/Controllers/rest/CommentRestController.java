package com.example.cybercert.Controllers.rest;

import com.example.cybercert.dto.CommentMapper;
import com.example.cybercert.dto.CommentDTO;
import com.example.cybercert.Models.Comment;
import com.example.cybercert.Services.CertificationService;
import com.example.cybercert.Services.CommentService;
import com.example.cybercert.Services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CertificationService certificationService;

    public CommentRestController(CommentService commentService,
            CommentMapper commentMapper,
            UserService userService,
            CertificationService certificationService) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.certificationService = certificationService;
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

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        var userOpt = userService.findByUsername(commentDTO.authorUsername());
        var certificationOpt = certificationService.findById(commentDTO.certificationId());

        if (userOpt.isEmpty() || certificationOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Comment newComment = commentService.addComment(
                userOpt.get(),
                certificationOpt.get(),
                commentDTO.content(),
                commentDTO.rating());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newComment.getId())
                .toUri();

        return ResponseEntity.created(location).body(commentMapper.toDTO(newComment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id,
            @RequestBody CommentDTO commentDTO) {
        return commentService.findById(id)
                .map(existing -> {
                    existing.setText(commentDTO.content());
                    existing.setRating(commentDTO.rating());
                    Comment updated = commentService.save(existing);
                    return ResponseEntity.ok(commentMapper.toDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id) {
        return commentService.findById(id)
                .map(comment -> {
                    commentService.deleteById(id);
                    return ResponseEntity.<Void>noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
