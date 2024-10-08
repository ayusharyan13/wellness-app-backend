package com.ayush.blog.repository;
import com.ayush.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


// we don't need to annotate with Repository annotation as it internally use the repository annotation and transaction
public interface PostRepository extends JpaRepository<Post,Long> {
    // by help of JpaRepository we get all the CRUD methods

    // to get all the posts by categoryId

    List<Post> findByCategoryId(Long categoryId);
}
