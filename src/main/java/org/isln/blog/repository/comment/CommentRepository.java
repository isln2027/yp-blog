package org.isln.blog.repository.comment;

import java.util.Collection;
import java.util.List;

import org.isln.blog.model.Comment;

public interface CommentRepository {
    Long create(Comment comment);

    void update(Comment comment);

    Comment findById(Long id);

    List<Comment> findByPostId(Long postId);

    void delete(Long id);

    void delete(Collection<Long> ids);
}
