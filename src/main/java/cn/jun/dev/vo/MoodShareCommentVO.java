package cn.jun.dev.vo;

import lombok.Data;
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
    
    /** 是否是分享作者 */
    private Boolean isOwner;
    
    /** 当前用户是否可以删除 */
    private Boolean canDelete;
    
    /** 回复对象的昵称 */
    private String replyToName;
}
