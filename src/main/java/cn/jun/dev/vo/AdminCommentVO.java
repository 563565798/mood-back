package cn.jun.dev.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员视角的评论VO
 */
@Data
public class AdminCommentVO {

    private Long id;

    private Long shareId;

    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    private String content;

    private String anonymousName;

    private Long parentId;

    private Integer isDeleted;

    private LocalDateTime createdAt;
}
