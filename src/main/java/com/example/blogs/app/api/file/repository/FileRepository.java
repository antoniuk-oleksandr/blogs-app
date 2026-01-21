package com.example.blogs.app.api.file.repository;

import com.example.blogs.app.api.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
