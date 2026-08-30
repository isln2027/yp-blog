package org.isln.blog.repository.post;

import java.util.Collection;
import java.util.List;

import org.isln.blog.model.Post;

public interface PostRepository {
    Post findById(Long id);
    List<Post> find(Integer pageNumber, Integer pageSize, PostRequestParameters parameters);
    Long create(Post post);
    void update(Post post);
    void delete(Long id);
    void delete(Collection<Long> ids);
    Long count(PostRequestParameters parameters);
    Integer incrementLikeCount(Long id);
    Boolean exists(Long id);
}