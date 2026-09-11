package org.isln.blog.controller.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Accessors(chain = true)
public class PagedPostDto {
    private List<PostDto> posts;
    @JsonProperty("hasPrev")
    private Boolean hasPrevious;
    private Boolean hasNext;
    private Integer lastPage;
}
