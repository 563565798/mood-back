package cn.jun.dev.vo;

import lombok.Data;

/**
 * 管理统计VO
 */
@Data
public class AdminStatsVO {

    /** 用户总数 */
    private Long totalUsers;

    /** 今日新增用户 */
    private Long todayNewUsers;

    /** 帖子总数 */
    private Long totalShares;

    /** 今日新增帖子 */
    private Long todayNewShares;

    /** 评论总数 */
    private Long totalComments;

    /** 今日新增评论 */
    private Long todayNewComments;

    /** 被删除的帖子数 */
    private Long deletedShares;

    /** 被删除的评论数 */
    private Long deletedComments;
}
