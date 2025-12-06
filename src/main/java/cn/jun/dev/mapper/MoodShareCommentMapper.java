package cn.jun.dev.mapper;

import cn.jun.dev.entity.MoodShareComment;
import cn.jun.dev.vo.MoodShareCommentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MoodShareCommentMapper {

        @Insert("INSERT INTO mood_share_comment(share_id, user_id, content, anonymous_name, parent_id, is_anonymous, created_at) "
                        +
                        "VALUES(#{shareId}, #{userId}, #{content}, #{anonymousName}, #{parentId}, #{isAnonymous}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(MoodShareComment comment);

        @Update("UPDATE mood_share_comment SET is_deleted = 1 WHERE id = #{id}")
        int deleteById(Long id);

        @Select("SELECT * FROM mood_share_comment WHERE id = #{id} AND is_deleted = 0")
        MoodShareComment findById(Long id);

        /**
         * 查询分享的评论列表（带用户信息，非匿名评论返回用户信息）
         */
        @Select("SELECT msc.*, " +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.username ELSE NULL END as username, "
                        +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.nickname ELSE NULL END as nickname, "
                        +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.avatar ELSE NULL END as avatar, "
                        +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.gender ELSE NULL END as gender, "
                        +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.birthday ELSE NULL END as birthday, "
                        +
                        "CASE WHEN msc.is_anonymous = 0 OR msc.is_anonymous IS NULL THEN u.created_at ELSE NULL END as user_created_at "
                        +
                        "FROM mood_share_comment msc " +
                        "LEFT JOIN user u ON msc.user_id = u.id " +
                        "WHERE msc.share_id = #{shareId} AND msc.is_deleted = 0 ORDER BY msc.created_at ASC")
        List<MoodShareCommentVO> findByShareId(Long shareId);

        @Select("SELECT * FROM mood_share_comment WHERE share_id = #{shareId} AND user_id = #{userId} LIMIT 1")
        MoodShareComment findByShareIdAndUserId(@Param("shareId") Long shareId, @Param("userId") Long userId);

        /**
         * 分页查询所有评论（管理员，包含已删除）
         */
        @Select("SELECT msc.*, u.username, u.nickname FROM mood_share_comment msc " +
                        "LEFT JOIN user u ON msc.user_id = u.id " +
                        "ORDER BY msc.created_at DESC LIMIT #{offset}, #{pageSize}")
        List<MoodShareComment> findAllWithPageForAdmin(@Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计所有评论总数（管理员，包含已删除）
         */
        @Select("SELECT COUNT(*) FROM mood_share_comment")
        Long countAllForAdmin();

        /**
         * 更新删除状态（管理员）
         */
        @Update("UPDATE mood_share_comment SET is_deleted = #{isDeleted} WHERE id = #{id}")
        int updateDeleteStatus(@Param("id") Long id, @Param("isDeleted") Integer isDeleted);
}
