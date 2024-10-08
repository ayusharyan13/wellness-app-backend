package com.ayush.blog.service;

import com.ayush.blog.dto.CommentDto;
import com.ayush.blog.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, CommentDto commentDto);
    List<CommentDto> getCommentsByPostId(long postId);
    CommentDto getCommentById(long postId,long commentId);

    CommentDto updateComment(long postId,long commentId,CommentDto commentRequest);

    String deleteComment(long postId, long commentId);
}
