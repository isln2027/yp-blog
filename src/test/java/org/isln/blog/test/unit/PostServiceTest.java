package org.isln.blog.test.unit;

import java.util.Collections;

import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.service.PostService;
import org.isln.blog.service.dto.PagedPosts;
import org.isln.blog.test.unit.configuration.TestConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig(classes = {TestConfiguration.class})
public class PostServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @ParameterizedTest
    @CsvSource({
            "0, 5, 0",
            "3, 5, 0",
            "5, 5, 0",
            "7, 5, 1",
            "10, 5, 1"
    })
    public void pageCountTest(
            long totalPosts,
            int pageSize,
            int expectedLastPage) {
        Mockito.when(postRepository.find(any(), any(), any()))
                .thenReturn(Collections.nCopies(pageSize, new Post()));
        Mockito.when(postRepository.count(any())).thenReturn(totalPosts);

        PagedPosts posts = postService.find(null, 0, pageSize);

        assertThat(posts.getLastPage()).isEqualTo(expectedLastPage);
    }

    @ParameterizedTest
    @CsvSource({
            "0, false",
            "1, true",
            "2, true",
    })
    public void hasPreviousTest(int currentPage, boolean hasPrevious) {
        Mockito.when(postRepository.find(any(), any(), any()))
                .thenReturn(Collections.nCopies(10, new Post()));
        Mockito.when(postRepository.count(any())).thenReturn(20L);

        PagedPosts posts = postService.find("", currentPage, 5);

        assertThat(posts.hasPrevious()).isEqualTo(hasPrevious);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 3, 7, true",
            "1, 3, 7, true",
            "2, 3, 7, false",
            "2, 3, 9, false"
    })
    public void hasNextTest(int currentPage, int pageSize, long totalPostCount, boolean hasNext) {
        Mockito.when(postRepository.find(any(), any(), any()))
                .thenReturn(Collections.nCopies(pageSize, new Post()));
        Mockito.when(postRepository.count(any())).thenReturn(totalPostCount);

        PagedPosts posts = postService.find("", currentPage, pageSize);

        assertThat(posts.hasNext()).isEqualTo(hasNext);
    }

}
