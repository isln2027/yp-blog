package org.isln.blog.controller;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.blog.controller.dto.CommentDto;
import org.isln.blog.controller.dto.PagedPostDto;
import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.service.CommentService;
import org.isln.blog.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final EntityMapper mapper;

    @PostMapping("/posts")
    public PostDto create(@RequestBody PostDto post) {
        return mapper.map(postService.create(mapper.map(post)));
    }

    @GetMapping("/posts")
    public PagedPostDto find(
            @RequestParam Integer pageNumber,
            @RequestParam Integer pagSize,
            @RequestParam(required = false) String search
    ) {
        return mapper.map(postService.find(search, pageNumber, pagSize));
    }

    @GetMapping("/posts/{id}")
    public PostDto find(@PathVariable Long id) {
        return mapper.map(postService.findById(id));
    }

    @PutMapping("/posts/{id}/image")
    public ResponseEntity<byte[]> setImage(@RequestBody MultipartFile file, @PathVariable Long id) throws IOException {
        byte[] image = postService.setImage(id, file.getName(), file.getBytes());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(image);
    }

    @GetMapping("/posts/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = postService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(image);
    }

    @PutMapping("/posts/{id}")
    public PostDto update(@RequestBody PostDto post, @PathVariable Long id) {
        postService.update(mapper.map(post, id));
        return mapper.map(postService.findById(id));
    }

    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    @PostMapping("/posts/{id}/likes")
    public Integer like(@PathVariable Long id) {
        return postService.like(id);
    }

    @PostMapping("/posts/{id}/comments")
    public CommentDto addComment(@PathVariable Long id, @RequestBody CommentDto comment) {
        Long commentId = commentService.create(mapper.map(comment.setPostId(id)));
        return mapper.map(commentService.findById(commentId));
    }

    @GetMapping("/posts/{id}/comments")
    public List<CommentDto> findComments(@PathVariable Long id) {
        return commentService.findByPostId(id).stream().map(mapper::map).toList();
    }

    @GetMapping("/posts/{id}/comments/{commentId}")
    public CommentDto findComment(@PathVariable Long commentId) {
        return mapper.map(commentService.findById(commentId));
    }

    @DeleteMapping("/posts/{id}/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }
}
