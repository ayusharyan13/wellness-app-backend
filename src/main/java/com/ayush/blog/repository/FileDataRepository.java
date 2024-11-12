package com.ayush.blog.repository;

import com.ayush.blog.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileData,Integer> {

}
