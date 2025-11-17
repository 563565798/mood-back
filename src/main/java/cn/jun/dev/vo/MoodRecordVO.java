package cn.jun.dev.vo;

import cn.jun.dev.entity.MoodType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 情绪记录VO
 */
@Data
public class MoodRecordVO {
    
    private Long id;
    
    private Long userId;
    
    private Long moodTypeId;
    
    private Integer intensity;
    
    private String triggerEvent;
    
    private String thoughts;
    
    private String location;
    
    private String weather;
    
    private String tags;
    
    private String images;
    
    private Integer isPrivate;
    
    private LocalDate recordDate;
    
    private LocalTime recordTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * 情绪类型信息
     */
    private MoodType moodType;
}




