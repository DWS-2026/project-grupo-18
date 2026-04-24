package com.example.cybercert.Services;

import org.springframework.stereotype.Service;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.Comment;
import com.example.cybercert.Models.User;
import com.example.cybercert.Repositories.CommentsRepository;

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

    public Optional<Comment> findById(Long id) {
        return commentsRepository.findById(id);
    }

    public List<Comment> getCommentsByCertification(Long certificationId) {
        return commentsRepository.findByCertificationIdOrderByCreatedAtDesc(certificationId);
    }

    public Comment addComment(User user, Certification certification, String text, int rating) {
        Comment comment = new Comment(user, certification, text, rating);
        return commentsRepository.save(comment);
    }
}
