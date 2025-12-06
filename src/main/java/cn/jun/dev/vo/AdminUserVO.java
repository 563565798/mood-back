package cn.jun.dev.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理员视角的用户VO
 */
@Data
public class AdminUserVO {

    private Long id;

    private String username;

    private String email;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private Integer status;

    private Integer role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 分享帖子数 */
    private Long shareCount;

    /** 评论数 */
    private Long commentCount;
}
