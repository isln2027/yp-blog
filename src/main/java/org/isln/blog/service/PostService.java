package org.isln.blog.service;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.isln.blog.exceptions.ObjectNotFound;
import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.isln.blog.service.dto.PagedPosts;
import org.isln.blog.service.file.FileService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final FileService fileService;

    public Post create(Post post) {
        Long id = postRepository.create(post);
        return findById(id);
    }

    public PagedPosts find(String query, Integer pageNumber, Integer pageSize) {
        String search = PostQueryResolver.getSearchQuery(query);
        Set<String> tags = PostQueryResolver.getTags(query);
        PostRequestParameters parameters = new PostRequestParameters(search, tags);
        List<Post> posts = postRepository.find(pageNumber, pageSize, parameters);

        long totalPostCount = postRepository.count(parameters);
        boolean hasPrevious = pageNumber > 0;
        int pageCount = countPages(pageSize, totalPostCount);
        int lastPage = pageCount - 1;
        boolean hasNext = pageNumber < lastPage;

        return new PagedPosts()
                .setPosts(posts)
                .setHasPrevious(hasPrevious)
                .setHasNext(hasNext)
                .setLastPage(Math.max(0, lastPage));
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

    public byte[] setImage(Long id, String fileName, byte[] file) {
        if (!postRepository.exists(id)) {
            throw new ObjectNotFound("Post '" + id + "' not found");
        }
        String name = id + "";
        fileService.save(name, file);
        return fileService.get(name);
    }

    public byte[] getImage(Long id) {
        String name = id + "";
        return fileService.get(name);
    }

    private static int countPages(int pageSize, long totalPostCount) {
        return (int) ((totalPostCount + pageSize - 1) / pageSize);
    }
}
