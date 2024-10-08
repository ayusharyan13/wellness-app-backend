package com.ayush.blog.service;

import com.ayush.blog.dto.PostDto;
import com.ayush.blog.dto.PostResponse;
import com.ayush.blog.entity.Post;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponse getAllPosts(int pageNo, int pageSize,String sortBy,String sortDir);

    PostDto getPostById(long id);
    PostDto updatePost(PostDto postDto,long id);

    String deletePostById(long id);

    List<PostDto> getPostsByCategory(Long categoryId);
}
