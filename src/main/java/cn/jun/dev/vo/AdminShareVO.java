package cn.jun.dev.vo;

import cn.jun.dev.entity.MoodType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员视角的分享VO
 */
@Data
public class AdminShareVO {

    private Long id;

    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    private Long moodTypeId;

    private String content;

    private String anonymousName;

    private Integer likeCount;

    private Integer commentCount;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    /** 情绪类型信息 */
    private MoodType moodType;
}
