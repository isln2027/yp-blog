package org.isln.blog.test.integration;

import java.util.List;
import java.util.Set;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, ApplicationConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    public void postCreationTest() {
        String text = "text";
        String title = "title";
        final String tag1 = "tag1";
        final String tag2 = "tag2";
        Post postToCreate = new Post().setTitle(title).setText(text).setTags(Set.of(tag1, tag2));

        Long id = postRepository.create(postToCreate);

        Post createdPost = postRepository.findById(id);
        assertThat(createdPost.getTitle()).isEqualTo(title);
        assertThat(createdPost.getText()).isEqualTo(text);
        assertThat(createdPost.getTags()).containsExactly(tag1, tag2);
        assertThat(createdPost.getCommentCount()).isEqualTo(0);
        assertThat(createdPost.getLikeCount()).isEqualTo(0);
    }

    @Test
    public void pagingRequestTest() {
        createPosts(6);

        List<Post> posts = postRepository.find(new PostRequestParameters(1, 2, null, null));

        assertThat(posts).hasSize(2);
        assertThat(posts).anyMatch(post -> post.getId().equals(3L));
        assertThat(posts).anyMatch(post -> post.getId().equals(4L));
    }

    private void createPosts(int amount) {
        for (int i = 0; i < amount; i++) {
            String text = "text " + i;
            String title = "title " + i;
            final String tag1 = "tag1";
            final String tag2 = "tag2";
            postRepository.create(new Post().setTitle(title).setText(text).setTags(Set.of(tag1, tag2)));
        }
    }
}
