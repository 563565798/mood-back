package cn.jun.dev.vo;

import lombok.Data;

@Data
public class MoodSummaryVO {
    /**
     * 总结文本
     */
    private String summaryText;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 主导情绪
     */
    private String dominantMood;

    /**
     * 平均强度
     */
    private Double avgIntensity;

    /**
     * 记录次数
     */
    private Integer recordCount;
}
