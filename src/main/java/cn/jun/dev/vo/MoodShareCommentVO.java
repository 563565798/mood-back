package cn.jun.dev.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MoodShareCommentVO {
    private Long id;
    private Long shareId;
    private Long userId;
    private String content;
    private String anonymousName;
    private Long parentId;
    private LocalDateTime createdAt;

    /** 是否匿名 */
    private Integer isAnonymous;

    /** 是否是分享作者 */
    private Boolean isOwner;

    /** 当前用户是否可以删除 */
    private Boolean canDelete;

    /** 回复对象的昵称 */
    private String replyToName;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户头像 */
    private String avatar;

    /** 用户性别 */
    private Integer gender;

    /** 用户生日 */
    private LocalDate birthday;

    /** 用户注册时间 */
    private LocalDateTime userCreatedAt;
}
