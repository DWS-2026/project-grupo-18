package com.example.cybercert;

import org.springframework.stereotype.Service;
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
