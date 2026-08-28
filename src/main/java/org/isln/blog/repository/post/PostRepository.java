package org.isln.blog.repository.post;

import java.util.List;

import org.isln.blog.model.Post;

public interface PostRepository {
    Post findById(long id);
    List<Post> find(int pageNumber, int pageSize, PostRequestParameters parameters);
    Long create(Post post);
    void update(Post post);
    void delete(long id);
    Long count(PostRequestParameters parameters);
}
