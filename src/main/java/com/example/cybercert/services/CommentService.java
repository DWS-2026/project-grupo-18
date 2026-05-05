package com.example.cybercert.services;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.cybercert.models.Certification;
import com.example.cybercert.models.Comment;
import com.example.cybercert.models.User;
import com.example.cybercert.repositories.CommentsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentsRepository commentsRepository;

    public List<Comment> findAll() {
        return commentsRepository.findAll();
    }

    public Page<Comment> findAll(Pageable pageable) {
        return commentsRepository.findAll(pageable);
    }

    public Optional<Comment> findById(Long id) {
        return commentsRepository.findById(id);
    }

    public Comment save(Comment comment) {
        return commentsRepository.save(comment);
    }

    public void deleteById(Long id) {
        commentsRepository.deleteById(id);
    }

    public List<Comment> getCommentsByCertification(Long certificationId) {
        return commentsRepository.findByCertificationIdOrderByCreatedAtDesc(certificationId);
    }

    public Page<Comment> getCommentsByCertification(Long certificationId, Pageable pageable) {
        return commentsRepository.findByCertificationIdOrderByCreatedAtDesc(certificationId, pageable);
    }

    public Comment addComment(User user, Certification certification, String text, int rating) {
        Comment comment = new Comment(user, certification, text, rating);
        return commentsRepository.save(comment);
    }
}
