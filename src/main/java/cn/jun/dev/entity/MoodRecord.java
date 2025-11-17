package cn.jun.dev.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 情绪记录实体
 */
@Data
public class MoodRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 情绪类型ID
     */
    private Long moodTypeId;
    
    /**
     * 情绪强度：1-10
     */
    private Integer intensity;
    
    /**
     * 触发事件描述
     */
    private String triggerEvent;
    
    /**
     * 当时的想法
     */
    private String thoughts;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 天气
     */
    private String weather;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 图片URL（JSON数组）
     */
    private String images;
    
    /**
     * 是否私密：0-公开，1-私密
     */
    private Integer isPrivate;
    
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    
    /**
     * 记录时间
     */
    private LocalTime recordTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}




