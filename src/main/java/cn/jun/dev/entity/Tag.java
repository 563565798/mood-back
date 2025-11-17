package cn.jun.dev.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签实体
 */
@Data
public class Tag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 分类：work-工作，family-家庭，health-健康，social-社交，other-其他
     */
    private String category;
    
    /**
     * 颜色
     */
    private String color;
    
    /**
     * 用户ID（NULL表示系统标签）
     */
    private Long userId;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}




