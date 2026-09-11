package org.isln.blog.controller.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PostDto {
    private Long id;
    private String title;
    private String text;
    private Set<String> tags;
    private Integer commentsCount;
    private Integer likesCount;
}
