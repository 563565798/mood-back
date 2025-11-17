package cn.jun.dev.service;

import cn.jun.dev.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
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
    
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    private Path baseUploadPath;
    
    @PostConstruct
    public void init() {
        // 如果配置的是相对路径，转换为绝对路径（相对于项目根目录）
        Path path = Paths.get(uploadPath);
        if (!path.isAbsolute()) {
            // 获取项目根目录（back目录）
            String projectRoot = System.getProperty("user.dir");
            baseUploadPath = Paths.get(projectRoot, uploadPath);
        } else {
            baseUploadPath = path;
        }
        
        // 确保基础目录存在
        try {
            Files.createDirectories(baseUploadPath);
            logger.info("文件上传目录初始化成功: {}", baseUploadPath.toAbsolutePath());
        } catch (IOException e) {
            logger.error("创建文件上传目录失败: {}", baseUploadPath.toAbsolutePath(), e);
            throw new RuntimeException("文件上传目录初始化失败", e);
        }
    }
    
    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
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
        Path uploadDir = baseUploadPath.resolve("images").resolve(dateFolder);
        try {
            Files.createDirectories(uploadDir);
            logger.debug("创建目录成功: {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            logger.error("创建目录失败: {}", uploadDir.toAbsolutePath(), e);
            throw new BusinessException(5000, "创建目录失败: " + e.getMessage());
        }
        
        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        try {
            file.transferTo(filePath.toFile());
            logger.info("文件上传成功: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            logger.error("文件保存失败: {}, 错误信息: {}", filePath.toAbsolutePath(), e.getMessage(), e);
            throw new BusinessException(5000, "文件保存失败: " + e.getMessage());
        }
        
        // 返回访问URL
        return contextPath + "/uploads/" + relativePath;
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

