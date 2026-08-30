package org.isln.blog.controller;

import org.isln.blog.controller.dto.PagedPostDto;
import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.model.Post;
import org.isln.blog.service.dto.PagedPosts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    PostDto map(Post post);

    @Mapping(target = "lastPage", expression = "java(posts.getLastPage() + 1)")
    PagedPostDto map(PagedPosts posts);

    @Mapping(target = "id", source = "id")
    Post map(PostDto postDto, Long id);

    Post map(PostDto postDto);
}
