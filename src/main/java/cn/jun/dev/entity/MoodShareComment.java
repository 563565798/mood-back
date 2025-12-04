package cn.jun.dev.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分享墙评论实体
 */
@Data
public class MoodShareComment {
    /** 主键ID */
    private Long id;
    
    /** 分享ID */
    private Long shareId;
    
    /** 用户ID */
    private Long userId;
    
    /** 评论内容 */
    private String content;
    
    /** 匿名昵称 */
    private String anonymousName;
    
    /** 父评论ID */
    private Long parentId;
    
    /** 是否删除 */
    private Integer isDeleted;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}
