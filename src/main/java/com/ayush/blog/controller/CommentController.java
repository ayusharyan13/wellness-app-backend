package com.ayush.blog.controller;

import com.ayush.blog.dto.CommentDto;
import com.ayush.blog.entity.Comment;
import com.ayush.blog.service.CommentService;
import com.ayush.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class CommentController {
    private CommentService commentService;
    private PostService postService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(name = "postId") long postId,
           @Valid @RequestBody CommentDto commentDto) {
        System.out.println("comment controller called");
        System.out.println("comment controller called and data passed is  postId:" + postId + " :  " + "and commentDto is " + commentDto );
        return new ResponseEntity<>(commentService.createComment(postId,commentDto), HttpStatus.CREATED);
    }



    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getAllComments(
            @PathVariable(value = "postId") long postId
    ){
        System.out.println("getting comment service");
        List<CommentDto> allComments = commentService.getCommentsByPostId(postId);
        return allComments;
    }


    @GetMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId) {
        CommentDto commentDto = commentService.getCommentById(postId,commentId);
        return new ResponseEntity<>(commentDto,HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment( @Valid
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId,
            @Valid @RequestBody CommentDto commentDto) {
        CommentDto updatedComment = commentService.updateComment(postId,commentId,commentDto);
        return new ResponseEntity<>(updatedComment,HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public String deleteCommentById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId
    ) {
        commentService.deleteComment(postId,commentId);
        return "comment deleted successfully !! don't worry ";
    }

}
