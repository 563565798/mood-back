package cn.jun.dev.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 私信实体
 */
@Data
public class PrivateMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 发送者ID */
    private Long senderId;

    /** 接收者ID */
    private Long receiverId;

    /** 消息内容 */
    private String content;

    /** 是否已读：0-未读，1-已读 */
    private Integer isRead;

    /** 发送者是否删除 */
    private Integer isDeletedBySender;

    /** 接收者是否删除 */
    private Integer isDeletedByReceiver;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
