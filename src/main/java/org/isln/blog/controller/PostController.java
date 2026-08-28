package org.isln.blog.controller;

import lombok.RequiredArgsConstructor;

import org.isln.blog.controller.dto.PagedPostDto;
import org.isln.blog.controller.dto.PostDto;
import org.isln.blog.service.PostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final EntityMapper mapper;
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

    @PutMapping("/posts/{id}")
    public PostDto update(@RequestBody PostDto post, @PathVariable Long id) {
        postService.update(mapper.map(post, id));
        return mapper.map(postService.findById(id));
    }

    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
