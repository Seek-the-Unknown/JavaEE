package com.controller;

import com.service.FileStorageService;
import com.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件管理", description = "用于上传和管理文件")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 存储文件并获取文件名
        String fileName = fileStorageService.storeFile(file);

        // 构建可访问的URL
        // 例如: http://localhost:8888/uploads/xxxxxxxx.jpg
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        return Result.success("文件上传成功", fileDownloadUri);
    }
}