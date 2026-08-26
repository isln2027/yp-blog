package org.isln.blog.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Post {
    private Long id;
    private String title;
    private String text;
    private Set<String> tags;
    private Integer commentCount;
    private Integer likeCount;
}
