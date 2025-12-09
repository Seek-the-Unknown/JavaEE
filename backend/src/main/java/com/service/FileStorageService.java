package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建用于存储上传文件的目录！", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. 生成一个唯一的文件名，防止重名
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 2. 检查文件名是否包含无效字符
            if (fileName.contains("..")) {
                throw new RuntimeException("抱歉！文件名包含无效的路径序列 " + fileName);
            }

            // 3. 将文件复制到目标位置
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 4. 返回文件名，用于后续拼接URL
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("无法存储文件 " + fileName + "。请再试一次！", ex);
        }
    }
}