package org.isln.blog.test.integration;

import java.util.Collections;
import java.util.List;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.exceptions.ObjectNotFound;
import org.isln.blog.exceptions.RepositoryException;
import org.isln.blog.model.Comment;
import org.isln.blog.model.Post;
import org.isln.blog.repository.comment.CommentRepository;
import org.isln.blog.repository.post.PostRepository;
import org.isln.blog.repository.post.PostRequestParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, ApplicationConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    public void commentCreationTest() {
        Long postId = createPost();
        String text = "text";
        Comment commentToCreate = new Comment().setText(text).setPostId(postId);

        Long id = commentRepository.create(commentToCreate);

        Comment createdComment = commentRepository.findById(id);
        assertThat(createdComment.getText()).isEqualTo(text);
    }

    @Test
    public void findByPostTest() {
        Long postId1 = createPost();
        String text1 = "text";
        Comment commentToCreate1 = new Comment().setText(text1).setPostId(postId1);
        Long postId2 = createPost();
        String text2 = "text";
        Comment commentToCreate2 = new Comment().setText(text2).setPostId(postId2);
        Long id1 = commentRepository.create(commentToCreate1);
        Long id2 = commentRepository.create(commentToCreate2);

        List<Comment> postComments1 = commentRepository.findByPostId(postId1);

        assertThat(postComments1.stream().map(Comment::getId).toList()).containsExactly(id1);
    }

    @Test
    public void updateTest() {
        Long postId = createPost();
        String text = "text";
        String updatedText = "text new";
        Comment commentToCreate = new Comment().setText(text).setPostId(postId);
        Long id = commentRepository.create(commentToCreate);
        Comment commentUpdate = new Comment().setId(id).setText(updatedText);
        commentRepository.update(commentUpdate);

        Comment createdComment = commentRepository.findById(id);
        assertThat(createdComment.getText()).isEqualTo(updatedText);
    }

    @Test
    public void createWithNoPostIdTest() {
        assertThatThrownBy(() -> commentRepository.create(new Comment())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateWithNoIdTest() {
        assertThatThrownBy(() -> commentRepository.update(new Comment())).isInstanceOf(RepositoryException.class);
    }

    @Test
    public void deleteTest() {
        Long postId = createPost();
        String text = "text";
        Comment commentToCreate = new Comment().setText(text).setPostId(postId);
        Long id = commentRepository.create(commentToCreate);

        commentRepository.delete(id);

        assertThatThrownBy(() -> commentRepository.findById(id)).isInstanceOf(ObjectNotFound.class);
    }

    private Long createPost() {
        return postRepository.create(
                new Post()
                        .setTitle("")
                        .setText("")
                        .setTags(Collections.emptySet())
        );
    }

    @AfterEach
    public void cleanup() {
        List<Long> postIds = postRepository.find(0, Integer.MAX_VALUE, new PostRequestParameters())
                .stream()
                .map(Post::getId)
                .toList();
        postRepository.delete(postIds);
    }
}