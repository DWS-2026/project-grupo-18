package com.example.cybercert.controllers.rest;

import com.example.cybercert.dto.CommentMapper;
import com.example.cybercert.models.Comment;
import com.example.cybercert.models.User;
import com.example.cybercert.services.CertificationService;
import com.example.cybercert.services.CommentService;
import com.example.cybercert.services.UserService;
import com.example.security.Role;
import com.example.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import com.example.cybercert.dto.CommentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.security.jwt.JwtTokenProvider;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CertificationService certificationService;
    private final JwtTokenProvider jwtTokenProvider;

    public CommentRestController(CommentService commentService,
            CommentMapper commentMapper,
            UserService userService,
            CertificationService certificationService,
            JwtTokenProvider jwtTokenProvider) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.certificationService = certificationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<Page<CommentDTO>> getAllComments(
            @RequestParam(required = false) Long certificationId,
            Pageable pageable) {
        Page<Comment> comments = certificationId == null
                ? commentService.findAll(pageable)
                : commentService.getCommentsByCertification(certificationId, pageable);
        Page<CommentDTO> dtoPage = comments.map(commentMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(commentMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(HttpServletRequest request, @RequestBody CommentDTO commentDTO) {

        var userOpt = userService.findByUsername(commentDTO.authorUsername());
        var certificationOpt = certificationService.findById(commentDTO.certificationId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (certificationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Claims claims = jwtTokenProvider.validateToken(request, true);
            String username = claims.getSubject();
            if (!username.equals(commentDTO.authorUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    public ResponseEntity<CommentDTO> updateComment(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody CommentDTO commentDTO) {

        try {
            Claims claims = jwtTokenProvider.validateToken(request, true);
            String username = claims.getSubject();

            Comment existing = commentService.findById(id)
                    .orElse(null);

            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            if (existing.getUser() == null ||
                    !existing.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            existing.setText(commentDTO.content());
            existing.setRating(commentDTO.rating());

            Comment updated = commentService.save(existing);

            return ResponseEntity.ok(commentMapper.toDTO(updated));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteComment(
            HttpServletRequest request,
            @PathVariable Long id) {

        try {
            Claims claims = jwtTokenProvider.validateToken(request, true);
            String username = claims.getSubject();

            User loggedUser = userService.findByUsername(username).orElse(null);

            return commentService.findById(id)
                    .map(comment -> {
                        if (loggedUser.getRole() != Role.ADMIN
                                && !comment.getUser().getUsername().equals(username)) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }

                        commentService.deleteById(id);
                        return ResponseEntity.noContent().build();
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
