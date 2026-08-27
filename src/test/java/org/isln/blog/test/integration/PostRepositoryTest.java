package org.isln.blog.test.integration;

import java.util.Set;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
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
}
