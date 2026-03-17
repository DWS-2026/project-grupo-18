package com.example.cybercert.Services;

import org.springframework.stereotype.Service;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.Comment;
import com.example.cybercert.Models.User;
import com.example.cybercert.Repositories.CommentsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentsRepository commentsRepository;

    public List<Comment> getCommentsByCertification(Long certificationId) {
        return commentsRepository.findByCertificationIdOrderByCreatedAtDesc(certificationId);
    }

    public Comment addComment(User user, Certification certification, String text) {
        Comment comment = new Comment(user, certification, text);
        return commentsRepository.save(comment);
    }
}
