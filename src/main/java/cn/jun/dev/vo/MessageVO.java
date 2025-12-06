package cn.jun.dev.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 私信VO
 */
@Data
public class MessageVO {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Integer isRead;

    private LocalDateTime createdAt;

    /** 发送者用户名 */
    private String senderUsername;

    /** 发送者昵称 */
    private String senderNickname;

    /** 发送者头像 */
    private String senderAvatar;

    /** 接收者用户名 */
    private String receiverUsername;

    /** 接收者昵称 */
    private String receiverNickname;

    /** 接收者头像 */
    private String receiverAvatar;
}
