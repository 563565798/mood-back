package cn.jun.dev.vo;

import lombok.Data;
import java.util.List;

/**
 * 情绪预警VO
 */
@Data
public class MoodWarningVO {
    
    /**
     * 是否有预警
     */
    private boolean hasWarning;
    
    /**
     * 预警信息
     */
    private String message;
    
    /**
     * 建议列表
     */
    private List<String> suggestions;
}
