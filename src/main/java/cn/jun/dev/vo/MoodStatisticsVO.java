package cn.jun.dev.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 情绪统计VO
 */
@Data
public class MoodStatisticsVO {
    
    /**
     * 总记录数
     */
    private Long totalRecords;
    
    /**
     * 连续记录天数
     */
    private Integer continuousDays;
    
    /**
     * 情绪分布（情绪类型 -> 次数）
     */
    private Map<String, Integer> moodDistribution;
    
    /**
     * 情绪趋势（日期 -> 平均强度）
     */
    private List<MoodTrendItem> moodTrend;
    
    /**
     * 标签统计（标签 -> 次数）
     */
    private Map<String, Integer> tagStatistics;
    
    /**
     * 一周情绪分布
     */
    private Map<String, Double> weekdayMood;
    
    /**
     * 正面/负面情绪比例
     */
    private MoodRatioVO moodRatio;
    
    @Data
    public static class MoodTrendItem {
        private String date;
        private Double avgIntensity;
        private String moodName;
    }
    
    @Data
    public static class MoodRatioVO {
        private Integer positiveCount;
        private Integer negativeCount;
        private Integer neutralCount;
        private Double positiveRatio;
        private Double negativeRatio;
        private Double neutralRatio;
    }
}




