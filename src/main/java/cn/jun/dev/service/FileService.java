package cn.jun.dev.service;

import cn.jun.dev.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务
 */
@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        log.info("开始上传图片: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

        // 验证文件
        validateFile(file);

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;

        // 按日期分组存储
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "images/" + dateFolder + "/" + filename;

        // 创建目录
        Path uploadDir = Paths.get(uploadPath, "images", dateFolder).toAbsolutePath();
        log.info("上传目录: {}", uploadDir);

        try {
            Files.createDirectories(uploadDir);
            log.info("目录创建成功: {}", uploadDir);
        } catch (IOException e) {
            log.error("创建目录失败: {}", uploadDir, e);
            throw new BusinessException(5000, "创建目录失败: " + e.getMessage());
        }

        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        log.info("文件保存路径: {}", filePath);

        try {
            file.transferTo(filePath.toFile());
            log.info("文件保存成功: {}", filePath);
        } catch (IOException e) {
            log.error("文件保存失败: {}", filePath, e);
            throw new BusinessException(5000, "文件保存失败: " + e.getMessage());
        }

        // 返回访问URL
        String fileUrl = contextPath + "/uploads/" + relativePath;
        log.info("文件上传成功，访问URL: {}", fileUrl);
        return fileUrl;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(3001, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(3001, "文件大小不能超过5MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(3001, "文件名不能为空");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new BusinessException(3001, "只支持上传 jpg、jpeg、png、gif、webp 格式的图片");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
