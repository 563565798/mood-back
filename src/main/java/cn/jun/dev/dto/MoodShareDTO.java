package cn.jun.dev.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 心情分享DTO
 */
@Data
public class MoodShareDTO {
    
    @NotNull(message = "情绪类型不能为空")
    private Long moodTypeId;
    
    @NotBlank(message = "分享内容不能为空")
    @Size(max = 500, message = "分享内容不能超过500字")
    private String content;
    
    @Size(max = 20, message = "匿名昵称不能超过20字")
    private String anonymousName;
}



