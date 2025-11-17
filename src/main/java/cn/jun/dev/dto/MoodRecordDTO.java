package cn.jun.dev.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 情绪记录DTO
 */
@Data
public class MoodRecordDTO {
    
    @NotNull(message = "情绪类型不能为空")
    private Long moodTypeId;
    
    @NotNull(message = "情绪强度不能为空")
    @Min(value = 1, message = "情绪强度最小为1")
    @Max(value = 10, message = "情绪强度最大为10")
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
}



