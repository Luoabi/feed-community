package org.xingchang.brapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xingchang.brapi.annotation.OperationLog;
import org.xingchang.brapi.common.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传Controller
 */
@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${file.upload.domain:http://localhost:8080/api}")
    private String uploadDomain;
    
    /**
     * 上传单个图片
     */
    @Operation(summary = "上传图片")
    @OperationLog(module = "文件上传", operationType = "upload", description = "上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（最大5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过5MB");
            }
            
            // 保存文件并返回URL
            String fileUrl = saveFile(file, "images");
            return Result.success(fileUrl);
            
        } catch (Exception e) {
            log.error("上传图片失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量上传图片
     */
    @Operation(summary = "批量上传图片")
    @OperationLog(module = "文件上传", operationType = "upload", description = "批量上传图片")
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }
            
            if (files.length > 9) {
                return Result.error("最多只能上传9张图片");
            }
            
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                // 验证文件
                if (file.isEmpty()) {
                    continue;
                }
                
                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    continue;
                }
                
                // 验证文件大小（最大5MB）
                if (file.getSize() > 5 * 1024 * 1024) {
                    continue;
                }
                
                // 保存文件
                String fileUrl = saveFile(file, "images");
                urls.add(fileUrl);
            }
            
            return Result.success(urls);
            
        } catch (Exception e) {
            log.error("批量上传图片失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传视频
     */
    @Operation(summary = "上传视频")
    @OperationLog(module = "文件上传", operationType = "upload", description = "上传视频")
    @PostMapping("/video")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return Result.error("只能上传视频文件");
            }
            
            // 验证文件大小（最大50MB）
            if (file.getSize() > 50 * 1024 * 1024) {
                return Result.error("视频大小不能超过50MB");
            }
            
            // 保存文件并返回URL
            String fileUrl = saveFile(file, "videos");
            return Result.success(fileUrl);
            
        } catch (Exception e) {
            log.error("上传视频失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传头像
     */
    @Operation(summary = "上传头像")
    @OperationLog(module = "文件上传", operationType = "upload", description = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（最大2MB，头像不需要太大）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("头像大小不能超过2MB");
            }
            
            // 保存文件并返回URL
            String fileUrl = saveFile(file, "avatars");
            return Result.success(fileUrl);
            
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存文件到本地
     * @param file 文件
     * @param subDir 子目录（images/videos）
     * @return 文件访问URL
     */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "file";
        }
        
        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        
        // 生成新文件名：UUID + 扩展名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        
        // 按日期创建子目录：uploads/images/2024/04/12/
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = subDir + "/" + dateDir + "/" + newFilename;
        
        // ✅ 使用绝对路径或项目根目录
        Path uploadBasePath;
        if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
            // 已经是绝对路径
            uploadBasePath = Paths.get(uploadPath);
        } else {
            // 相对路径，使用项目根目录
            uploadBasePath = Paths.get(System.getProperty("user.dir"), uploadPath);
        }
        
        // 创建完整的文件路径
        Path uploadDir = uploadBasePath.resolve(subDir).resolve(dateDir);
        Files.createDirectories(uploadDir);
        
        // 保存文件
        Path filePath = uploadDir.resolve(newFilename);
        file.transferTo(filePath.toFile());
        
        log.info("文件保存成功: {}", filePath.toAbsolutePath());
        
        // 返回访问URL
        return uploadDomain + "/uploads/" + relativePath;
    }
}
