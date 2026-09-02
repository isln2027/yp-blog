package org.isln.blog.controller.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Accessors(chain = true)
public class PostDto {
    private Long id;
    private String title;
    private String text;
    private Set<String> tags;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("like_count")
    private Integer likeCount;
}
