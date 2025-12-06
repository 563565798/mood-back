package cn.jun.dev.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 心情分享墙实体
 */
@Data
public class MoodShare implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID（匿名显示）
     */
    private Long userId;

    /**
     * 情绪类型ID
     */
    private Long moodTypeId;

    /**
     * 分享内容
     */
    private String content;

    /**
     * 匿名昵称
     */
    private String anonymousName;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否匿名发布：0-否，1-是
     */
    private Integer isAnonymous;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
