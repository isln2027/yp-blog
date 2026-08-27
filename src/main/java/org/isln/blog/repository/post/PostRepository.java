package org.isln.blog.repository.post;

import java.util.List;

import org.isln.blog.model.Post;

public interface PostRepository {
    Post findById(long id);
    List<Post> find(PostRequestParameters postRequestParameters);
    Long create(Post post);
    void update(Post post);
    void delete(long id);
}
