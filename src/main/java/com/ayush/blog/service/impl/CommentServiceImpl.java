package com.ayush.blog.service.impl;
import com.ayush.blog.dto.CommentDto;
import com.ayush.blog.entity.Comment;
import com.ayush.blog.entity.Post;
import com.ayush.blog.exception.BlogApiException;
import com.ayush.blog.exception.ResourceNotFoundException;
import com.ayush.blog.repository.CommentRepository;
import com.ayush.blog.repository.PostRepository;
import com.ayush.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private  PostRepository postRepository;
    private ModelMapper mapper;
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }



    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        System.out.println("comment service impl called");
        Comment comment = mapToEntity(commentDto);
        //retrieve post entity by id
        Post post = postRepository.findById(postId).orElseThrow( ()->
           new ResourceNotFoundException("post" , "id" , postId));


        // set post to comment entity
        comment.setPost(post);
        Comment newComment = commentRepository.save(comment);
        return mapTODto(newComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        // get comments by PostId
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(comment -> mapTODto(comment)).collect(Collectors.toList());
    }

    public CommentDto getCommentById(long postId, long commentId) {
        // retrieve post from db
        Post post = postRepository.findById(postId).orElseThrow( ()->
                new ResourceNotFoundException("post" , "id" , postId));
        Comment comment = commentRepository.findById(commentId).orElseThrow( () -> new ResourceNotFoundException("comment","id",commentId));

        // now check if this comment belong to that post or not:
        if(!comment.getPost().getId().equals(post.getId())) {
            throw new  BlogApiException(HttpStatus.BAD_REQUEST,"comment does not belong to Post");
        }
        return mapTODto(comment);
    }

    @Override
    public CommentDto updateComment(long postId, long commentId, CommentDto commentRequest) {
        // retrieve post from db
        Post post = postRepository.findById(postId).orElseThrow( ()->
                new ResourceNotFoundException("post" , "id" , postId));
        Comment comment = commentRepository.findById(commentId).orElseThrow( () -> new ResourceNotFoundException("comment","id",commentId));

        // now check if this comment belong to that post or not:
        if(!comment.getPost().getId().equals(post.getId())) {
            throw new  BlogApiException(HttpStatus.BAD_REQUEST,"comment does not belong to Post");
        }
        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setBody(commentRequest.getBody());

        Comment updatedComment = commentRepository.save(comment);
        return mapTODto(updatedComment);
    }

    @Override
    public String deleteComment(long postId, long commentId) {
        Post post = postRepository.findById(postId).orElseThrow( ()->
                new ResourceNotFoundException("post" , "id" , postId));
        Comment comment = commentRepository.findById(commentId).orElseThrow( () -> new ResourceNotFoundException("comment","id",commentId));

        // now check if this comment belong to that post or not:
        if(!comment.getPost().getId().equals(post.getId())) {
            throw new  BlogApiException(HttpStatus.BAD_REQUEST,"comment does not belong to Post");
        }

        commentRepository.deleteById(commentId);
        return "comment deleted successfully !!!";
    }

    private CommentDto mapTODto(Comment comment) {
        CommentDto commentDto = mapper.map(comment,CommentDto.class);
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setName(comment.getName());
//        commentDto.setEmail(comment.getEmail());
//        commentDto.setBody(comment.getBody());
        return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = mapper.map(commentDto,Comment.class);
//        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
//        comment.setBody(commentDto.getBody());
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
        return comment;
    }
}
