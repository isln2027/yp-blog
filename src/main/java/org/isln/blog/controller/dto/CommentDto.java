package org.isln.blog.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    private String text;
    private Long postId;
}
