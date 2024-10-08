package com.ayush.blog.entity;

import com.ayush.blog.dto.PostDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String body;


    // fetchType lazy tells the Hibernate to only get the required entity from the db
    @ManyToOne(fetch = FetchType.LAZY)  // one post have multiple comments:  multiple comments belong to one post
    @JoinColumn(name="post_id",nullable = false)  // join the foreign key in comments table the post_id
    private Post post;
}
