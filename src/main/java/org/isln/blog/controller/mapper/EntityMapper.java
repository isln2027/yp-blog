package org.isln.blog.controller.mapper;

import java.util.List;

import org.isln.blog.controller.dto.CommentDto;
import org.isln.blog.controller.dto.PagedPostDto;
import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.model.Comment;
import org.isln.blog.model.Post;
import org.isln.blog.service.dto.PagedPosts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class EntityMapper {
    public abstract PostDto map(Post post);

    @Mapping(target = "lastPage", expression = "java(posts.getLastPage() + 1)")
    @Mapping(target = "posts", expression = "java(mapPostsShort(posts.getPosts(), textCharCount))")
    public abstract PagedPostDto map(PagedPosts posts, int textCharCount);

    public List<PostDto> mapPostsShort(List<Post> posts, int textCharCount) {
        if (posts == null) {
            return null;
        }
        return posts.stream().map(p -> mapPostShort(p, textCharCount)).toList();
    }

    @Mapping(target = "id", source = "id")
    public abstract Post map(PostDto postDto, Long id);

    public abstract Post map(PostDto postDto);

    @Named("mapPostShort")
    @Mapping(target = "text", expression = "java(post.getTextShort(textCharCount))")
    public abstract PostDto mapPostShort(Post post, int textCharCount);

    public abstract Comment map(CommentDto commentDto);

    public abstract CommentDto map(Comment comment);
}
