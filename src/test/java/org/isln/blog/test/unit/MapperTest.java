package org.isln.blog.test.unit;

import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.controller.mapper.EntityMapper;
import org.isln.blog.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapperTest {
    private static final Integer MAX_TEXT_LENGTH = 5;

    @Autowired
    private EntityMapper mapper;

    @Test
    public void shortTextIsNotTruncatedText() {
        String shortText = "txt";
        Post post = new Post().setText(shortText);

        PostDto dto = mapper.mapPostShort(post, MAX_TEXT_LENGTH);

        String text = dto.getText();
        assertThat(text).isEqualTo(shortText);
    }


    @Test
    public void longTextIsTruncatedText() {
        String shortText = "txt" + "F".repeat(10);
        Post post = new Post().setText(shortText);

        PostDto dto = mapper.mapPostShort(post, MAX_TEXT_LENGTH);

        String text = dto.getText();
        assertThat(text).isEqualTo("txtFF...");
    }
}
