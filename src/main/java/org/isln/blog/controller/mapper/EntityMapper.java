package org.isln.blog.controller.mapper;

import lombok.Setter;

import org.isln.blog.controller.dto.CommentDto;
import org.isln.blog.controller.dto.PagedPostDto;
import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.model.Comment;
import org.isln.blog.model.Post;
import org.isln.blog.service.dto.PagedPosts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Setter
@Mapper(componentModel = "spring")
public abstract class EntityMapper {
    @Value("${app.max-text-length-in-paging-post-response:128}")
    protected Integer maxTextLengthInPagingPostResponse;

    public abstract PostDto map(Post post);

    @Mapping(target = "lastPage", expression = "java(posts.getLastPage() + 1)")
    @Mapping(target = "posts", source = "posts", qualifiedByName = "mapPostShort")
    public abstract PagedPostDto map(PagedPosts posts);

    @Mapping(target = "id", source = "id")
    public abstract Post map(PostDto postDto, Long id);

    public abstract Post map(PostDto postDto);

    @Named("mapPostShort")
    @Mapping(target = "text", expression = "java(truncateText(post.getText(), maxTextLengthInPagingPostResponse))")
    public abstract PostDto mapPostShort(Post post);

    public abstract Comment map(CommentDto commentDto);

    public abstract CommentDto map(Comment comment);

    protected String truncateText(String text, int charCount) {
        if (text == null) {
            return null;
        }
        if (text.length() < charCount) {
            return text;
        } else {
            return text.substring(0, charCount) + "...";
        }
    }
}

