package cn.jun.dev.vo;

import cn.jun.dev.entity.MoodType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 心情分享VO
 */
@Data
public class MoodShareVO {
    
    private Long id;
    
    private Long moodTypeId;
    
    private String content;
    
    private String anonymousName;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private LocalDateTime createdAt;
    
    /**
     * 情绪类型信息
     */
    private MoodType moodType;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
}




