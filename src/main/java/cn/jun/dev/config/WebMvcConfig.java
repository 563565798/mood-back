package cn.jun.dev.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    private String absoluteUploadPath;
    
    @PostConstruct
    public void init() {
        // 如果配置的是相对路径，转换为绝对路径（相对于项目根目录）
        Path path = Paths.get(uploadPath);
        if (!path.isAbsolute()) {
            // 获取项目根目录（back目录）
            String projectRoot = System.getProperty("user.dir");
            absoluteUploadPath = Paths.get(projectRoot, uploadPath).toAbsolutePath().toString();
        } else {
            absoluteUploadPath = path.toAbsolutePath().toString();
        }
        
        // 确保路径以 / 结尾
        if (!absoluteUploadPath.endsWith("/") && !absoluteUploadPath.endsWith("\\")) {
            absoluteUploadPath += "/";
        }
        
        logger.info("静态资源路径配置: /uploads/** -> file:{}", absoluteUploadPath);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadPath);
    }
}

