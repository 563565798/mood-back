package cn.jun.dev.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 情绪类型实体
 */
@Data
public class MoodType implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 情绪名称
     */
    private String name;
    
    /**
     * 图标（emoji或图标名称）
     */
    private String icon;
    
    /**
     * 颜色代码
     */
    private String color;
    
    /**
     * 分类：positive-积极，negative-消极，neutral-中性
     */
    private String category;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 是否系统预设：0-否，1-是
     */
    private Integer isSystem;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}




