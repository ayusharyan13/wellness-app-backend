package com.ayush.blog.repository;

import com.ayush.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository <Category,Long>{

}
