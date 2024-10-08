package com.ayush.blog.service;

import com.ayush.blog.dto.CategoryDto;
import com.ayush.blog.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    CategoryDto getCategory(Long categoryId);

    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    String deleteCategory(Long categoryId);
}
