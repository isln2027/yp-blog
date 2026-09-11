package org.isln.blog.test.integration;

import java.util.Collections;

import org.isln.blog.exceptions.FileOperationException;
import org.isln.blog.model.Post;
import org.isln.blog.service.PostService;
import org.isln.blog.service.file.FileService;
import org.isln.blog.test.integration.configuration.TestFileServiceConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

@SpringJUnitConfig(
        classes = {
                TestFileServiceConfiguration.class
        }
)
public class PostTransactionTest {
    @Autowired
    private FileService fileService;
    @Autowired
    private PostService postService;

    @Test
    public void postIsNotDeletedOnFileErrorTest() {
        doThrow(FileOperationException.class).when(fileService).delete(Mockito.any());
        Post post = postService.create(new Post().setTitle("").setText("").setTags(Collections.emptySet()));
        long id = post.getId();

        try {
            postService.delete(id);
        } catch (Exception ignored) {
        }

        assertThat(postService.findById(id).getId()).isEqualTo(id);
    }
}
