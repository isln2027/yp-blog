package org.isln.blog.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.isln.blog.service.dto.PagedPosts;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PagedPosts find(String query, Integer pageNumber, Integer pageSize) {
        // todo implement search
        PostRequestParameters parameters = new PostRequestParameters(null, null);
        List<Post> posts = postRepository.find(pageNumber, pageSize, parameters);

        long totalPostCount = postRepository.count(parameters);
        boolean hasPrevious = pageNumber > 0;
        int pageCount = countPages(pageSize, totalPostCount);
        boolean hasNext = pageNumber < pageCount;

        return new PagedPosts()
                .setPosts(posts)
                .setHasPrevious(hasPrevious)
                .setHasNext(hasNext)
                .setLastPage(Math.max(0, pageCount - 1));
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

    private static int countPages(int pageSize, long totalPostCount) {
        return (int) ((totalPostCount + pageSize - 1) / pageSize);
    }
}
