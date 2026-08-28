package org.isln.blog.service;

import org.isln.blog.model.Post;
import org.isln.blog.service.dto.PagedPosts;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    public PagedPosts find(String query, int pageNumber, int pageSize) {
        throw new RuntimeException("Not implemented");
    }

    public Post findById(Long id) {
        throw new RuntimeException("Not implemented");
    }

    public void update(Post post) {
        throw new RuntimeException("Not implemented");
    }

    public void delete(Long id) {
        throw new RuntimeException("Not implemented");
    }
}
