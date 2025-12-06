package cn.jun.dev.mapper;

import cn.jun.dev.entity.PrivateMessage;
import cn.jun.dev.vo.MessageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 私信Mapper
 */
@Mapper
public interface PrivateMessageMapper {

    /**
     * 发送私信
     */
    @Insert("INSERT INTO private_message(sender_id, receiver_id, content, created_at) " +
            "VALUES(#{senderId}, #{receiverId}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PrivateMessage message);

    /**
     * 查询收件箱（带发送者信息）
     */
    @Select("SELECT pm.*, " +
            "s.username as sender_username, s.nickname as sender_nickname, s.avatar as sender_avatar, " +
            "r.username as receiver_username, r.nickname as receiver_nickname, r.avatar as receiver_avatar " +
            "FROM private_message pm " +
            "LEFT JOIN user s ON pm.sender_id = s.id " +
            "LEFT JOIN user r ON pm.receiver_id = r.id " +
            "WHERE pm.receiver_id = #{userId} AND pm.is_deleted_by_receiver = 0 " +
            "ORDER BY pm.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<MessageVO> findInbox(@Param("userId") Long userId, @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize);

    /**
     * 查询发件箱（带接收者信息）
     */
    @Select("SELECT pm.*, " +
            "s.username as sender_username, s.nickname as sender_nickname, s.avatar as sender_avatar, " +
            "r.username as receiver_username, r.nickname as receiver_nickname, r.avatar as receiver_avatar " +
            "FROM private_message pm " +
            "LEFT JOIN user s ON pm.sender_id = s.id " +
            "LEFT JOIN user r ON pm.receiver_id = r.id " +
            "WHERE pm.sender_id = #{userId} AND pm.is_deleted_by_sender = 0 " +
            "ORDER BY pm.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<MessageVO> findOutbox(@Param("userId") Long userId, @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize);

    /**
     * 统计收件箱数量
     */
    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{userId} AND is_deleted_by_receiver = 0")
    Long countInbox(Long userId);

    /**
     * 统计发件箱数量
     */
    @Select("SELECT COUNT(*) FROM private_message WHERE sender_id = #{userId} AND is_deleted_by_sender = 0")
    Long countOutbox(Long userId);

    /**
     * 统计未读消息数
     */
    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{userId} AND is_read = 0 AND is_deleted_by_receiver = 0")
    Long countUnread(Long userId);

    /**
     * 标记为已读
     */
    @Update("UPDATE private_message SET is_read = 1 WHERE id = #{id} AND receiver_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 标记所有为已读
     */
    @Update("UPDATE private_message SET is_read = 1 WHERE receiver_id = #{userId} AND is_read = 0")
    int markAllAsRead(Long userId);

    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM private_message WHERE id = #{id}")
    PrivateMessage findById(Long id);
}
