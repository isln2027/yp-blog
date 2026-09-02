package org.isln.blog.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    private String text;
    @JsonProperty("post_id")
    private Long postId;
}
