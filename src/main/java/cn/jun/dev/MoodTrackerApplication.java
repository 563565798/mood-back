package cn.jun.dev;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 情绪记录与心理趋势可视化系统 - 启动类
 * @author haley
 */
@SpringBootApplication
@MapperScan("cn.jun.dev.mapper")
public class MoodTrackerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MoodTrackerApplication.class, args);
        System.out.println("========================================");
        System.out.println("情绪记录系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}




