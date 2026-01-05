package com.example.venue.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {

    // 这里的路径必须和 WebConfig 里配置的一致
    private static final String UPLOAD_DIR = "F:/venue_images/";

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件为空");
        }
        try {
            // 1. 生成唯一文件名，防止重名覆盖
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 2. 保存文件
            File dest = new File(UPLOAD_DIR + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            // 3. 返回可访问的 URL
            String fileUrl = "http://localhost:8888/images/" + newFileName;
            return Result.success(fileUrl);

        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}