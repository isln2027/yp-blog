package org.isln.blog.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.blog.model.Comment;
import org.isln.blog.repository.comment.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public Long create(Comment comment) {
        return commentRepository.create(comment);
    }

    public Comment update(Comment comment) {
        commentRepository.update(comment);
        return commentRepository.findById(comment.getId());
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void delete(Long id) {
        commentRepository.delete(id);
    }
}
