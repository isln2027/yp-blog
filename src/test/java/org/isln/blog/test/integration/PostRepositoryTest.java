package org.isln.blog.test.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.exceptions.ObjectNotFound;
import org.isln.blog.exceptions.RepositoryException;
import org.isln.blog.model.Post;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        List<Long> ids = createPosts(6);

        List<Post> posts = postRepository.find(1, 2, new PostRequestParameters( ));

        assertThat(posts).hasSize(2);
        assertThat(posts).anyMatch(post -> post.getId().equals(ids.get(2)));
        assertThat(posts).anyMatch(post -> post.getId().equals(ids.get(3)));
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

    @ParameterizedTest()
    @ValueSource(longs = {0, 5})
    public void countTest(long expectedPostCount) {
        createPosts(expectedPostCount);

        long actualPostCount = postRepository.count(new PostRequestParameters());

        assertThat(actualPostCount).isEqualTo(expectedPostCount);
    }

    @Test
    public void batchDeleteTest() {
        createPosts(3);
        List<Post> all = postRepository.find(0, 10, new PostRequestParameters());
        List<Long> toDelete = all.subList(0, 2).stream().map(Post::getId).toList();

        postRepository.delete(toDelete);

        List<Post> postsAfterDelete = postRepository.find(0, 10, new PostRequestParameters());
        assertThat(postsAfterDelete).hasSize(1);
        assertThat(postsAfterDelete.getFirst().getId()).isEqualTo(all.get(2).getId());
    }

    private List<Long> createPosts(long amount) {
        List<Long> ids = new ArrayList<>();
        for (long i = 0; i < amount; i++) {
            String text = "text " + i;
            String title = "title " + i;
            final String tag1 = "tag1";
            final String tag2 = "tag2";
            ids.add(postRepository.create(new Post().setTitle(title).setText(text).setTags(Set.of(tag1, tag2))));
        }
        return ids;
    }

    @BeforeEach
    public void cleanup() {
        List<Long> ids = postRepository.find(0, Integer.MAX_VALUE, new PostRequestParameters())
                .stream()
                .map(Post::getId)
                .toList();
        postRepository.delete(ids);
    }
}
