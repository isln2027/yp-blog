package org.isln.blog.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Comment {
    private Long id;
    private String text;
    private Long postId;
}
