package com.ayush.blog.service.impl;
import com.ayush.blog.dto.PostDto;
import com.ayush.blog.dto.PostResponse;
import com.ayush.blog.entity.Category;
import com.ayush.blog.entity.Post;
import com.ayush.blog.repository.CategoryRepository;
import com.ayush.blog.repository.PostRepository;
import com.ayush.blog.exception.ResourceNotFoundException;
import com.ayush.blog.service.CloudinaryService;
import com.ayush.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostServiceIMpl implements PostService {

    private PostRepository postRepository;
    private ModelMapper mapper;
    private CategoryRepository categoryRepository;
    private CloudinaryService cloudinaryService;

    // constructor based dependency injection
    public PostServiceIMpl(PostRepository postRepository, ModelMapper mapper, CategoryRepository categoryRepository, CloudinaryService cloudinaryService) {
        this.postRepository = postRepository;
        this.mapper =  mapper;
        this.categoryRepository = categoryRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public PostDto createPost(PostDto postDto,  MultipartFile imageFile) throws IOException {
        // get the category from db and add it to post:
        Category category = categoryRepository.findById(postDto.getCategoryId()).orElseThrow(
                ()-> new ResourceNotFoundException("category", "id", postDto.getCategoryId())
        );


        // Upload image and set URL
        String imageUrl = cloudinaryService.uploadFile(imageFile);
        Post post = mapToEntity(postDto);

        post.setCategory(category);
        post.setImageUrl(imageUrl);
        Post savedPost = postRepository.save(post);
        // convert newPost entity to postDto
        PostDto postResponse = mapToDto(savedPost);
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo,int pageSize,String sortBy,String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        // pageable instance
        PageRequest pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Post> posts = postRepository.findAll(pageable);
        // get content from page object
        List<Post> listOfPosts = posts.getContent();
        List<PostDto> content = listOfPosts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());
        return postResponse;
    }



    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow( () -> {
            return new ResourceNotFoundException("Post","id",id);
        });
        PostDto postWithId = mapToDto(post);
        return postWithId;
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        // get the post from the db by id: and then we will modify it : if not present then we will show exception
        Post post = postRepository.findById(id).orElseThrow( () -> {
            return new ResourceNotFoundException("Post","id",id);
        });

        // get the category from db and add it to post:
        Category category = categoryRepository.findById(postDto.getCategoryId()).orElseThrow(
                ()-> new ResourceNotFoundException("category", "id", postDto.getCategoryId())
        );


        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);
        Post updatedPost = postRepository.save(post);
        System.out.println(updatedPost);
        return mapToDto(updatedPost);
    }

    @Override
    public String deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow( ()-> {
            return new ResourceNotFoundException("post","id",id);});

        postRepository.deleteById(id);
        return "post deleted Successfully!!";
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {
        // get the category from db and add it to post:
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new ResourceNotFoundException("category", "id", categoryId)
        );

        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream().map((post) -> mapToDto(post)).collect(Collectors.toList());

    }


    //convert entity to dto
    private PostDto mapToDto(Post post) {
        PostDto postDto = mapper.map(post,PostDto.class);
        return postDto;
    }

    // convert dto to entity
    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto,Post.class);
        return post;
    }

}
