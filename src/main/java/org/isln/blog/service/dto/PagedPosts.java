package org.isln.blog.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.isln.blog.model.Post;

@Getter
@Setter
@Accessors(chain = true)
public class PagedPosts {
    private List<Post> posts;
    private Boolean hasPrevious;
    private Boolean hasNext;
    private Integer lastPage;
}
