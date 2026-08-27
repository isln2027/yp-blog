package org.isln.blog.test.integration;

import java.util.List;
import java.util.Set;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.exceptions.ObjectNotFound;
import org.isln.blog.exceptions.RepositoryException;
import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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

    @Test
    public void updateTest() {
        String text = "text";
        String title = "title";
        String tag1 = "tag1";
        String tag2 = "tag2";
        String updatedText = "text new";
        String updatedTitle = "title new";
        String updatedTag1 = "new_tag1";
        String updatedTag2 = "new_tag2";
        Post postToCreate = new Post().setTitle(title).setText(text).setTags(Set.of(tag1, tag2));
        Long id = postRepository.create(postToCreate);
        Post postUpdate = new Post()
                .setId(id)
                .setTitle(updatedTitle)
                .setText(updatedText)
                .setTags(Set.of(updatedTag1, updatedTag2));

        postRepository.update(postUpdate);

        Post createdPost = postRepository.findById(id);
        assertThat(createdPost.getTitle()).isEqualTo(updatedTitle);
        assertThat(createdPost.getText()).isEqualTo(updatedText);
        assertThat(createdPost.getTags()).containsExactly(updatedTag1, updatedTag2);
        assertThat(createdPost.getCommentCount()).isEqualTo(0);
        assertThat(createdPost.getLikeCount()).isEqualTo(0);
    }

    @Test
    public void updateWithNoIdTest() {
        assertThatThrownBy(() -> postRepository.update(new Post())).isInstanceOf(RepositoryException.class);
    }

    @Test
    public void deleteTest() {
        String text = "text";
        String title = "title";
        final String tag1 = "tag1";
        final String tag2 = "tag2";
        Post postToCreate = new Post().setTitle(title).setText(text).setTags(Set.of(tag1, tag2));
        Long id = postRepository.create(postToCreate);

        postRepository.delete(id);

        assertThatThrownBy(() -> postRepository.findById(id)).isInstanceOf(ObjectNotFound.class);
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
