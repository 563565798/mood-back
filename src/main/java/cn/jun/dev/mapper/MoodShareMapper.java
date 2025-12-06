package cn.jun.dev.mapper;

import cn.jun.dev.entity.MoodShare;
import cn.jun.dev.vo.MoodShareVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 心情分享Mapper
 */
@Mapper
public interface MoodShareMapper {

        /**
         * 插入分享
         */
        @Insert("INSERT INTO mood_share(user_id, mood_type_id, content, anonymous_name, is_anonymous, created_at) " +
                        "VALUES(#{userId}, #{moodTypeId}, #{content}, #{anonymousName}, #{isAnonymous}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(MoodShare share);

        /**
         * 删除分享（逻辑删除）
         */
        @Update("UPDATE mood_share SET is_deleted = 1 WHERE id = #{id}")
        int deleteById(Long id);

        /**
         * 根据ID查询分享
         */
        @Select("SELECT * FROM mood_share WHERE id = #{id} AND is_deleted = 0")
        MoodShare findById(Long id);

        /**
         * 分页查询分享列表（带情绪类型和用户信息）
         * 非匿名帖子返回用户信息，匿名帖子不返回
         */
        @Results(id = "moodShareWithType", value = {
                        @Result(property = "id", column = "id"),
                        @Result(property = "userId", column = "user_id"),
                        @Result(property = "moodType", column = "mood_type_id", one = @One(select = "cn.jun.dev.mapper.MoodTypeMapper.findById"))
        })
        @Select("SELECT ms.*, " +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.username ELSE NULL END as username, "
                        +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.nickname ELSE NULL END as nickname, "
                        +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.avatar ELSE NULL END as avatar, "
                        +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.gender ELSE NULL END as gender, "
                        +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.birthday ELSE NULL END as birthday, "
                        +
                        "CASE WHEN ms.is_anonymous = 0 OR ms.is_anonymous IS NULL THEN u.created_at ELSE NULL END as user_created_at "
                        +
                        "FROM mood_share ms " +
                        "LEFT JOIN user u ON ms.user_id = u.id " +
                        "WHERE ms.is_deleted = 0 " +
                        "ORDER BY ms.created_at DESC LIMIT #{offset}, #{pageSize}")
        List<MoodShareVO> findAllWithPage(@Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计分享总数
         */
        @Select("SELECT COUNT(*) FROM mood_share WHERE is_deleted = 0")
        Long countAll();

        /**
         * 增加点赞数
         */
        @Update("UPDATE mood_share SET like_count = like_count + 1 WHERE id = #{id}")
        int incrementLikeCount(Long id);

        /**
         * 减少点赞数
         */
        @Update("UPDATE mood_share SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
        int decrementLikeCount(Long id);

        /**
         * 增加评论数
         */
        @Update("UPDATE mood_share SET comment_count = comment_count + 1 WHERE id = #{id}")
        int incrementCommentCount(Long id);

        /**
         * 减少评论数
         */
        @Update("UPDATE mood_share SET comment_count = comment_count - 1 WHERE id = #{id} AND comment_count > 0")
        int decrementCommentCount(Long id);

        /**
         * 统计用户的分享总数
         */
        @Select("SELECT COUNT(*) FROM mood_share WHERE user_id = #{userId} AND is_deleted = 0")
        Long countByUserId(Long userId);

        /**
         * 分页查询所有分享（管理员，包含已删除）
         */
        @Select("SELECT ms.*, u.username, u.nickname FROM mood_share ms " +
                        "LEFT JOIN user u ON ms.user_id = u.id " +
                        "ORDER BY ms.created_at DESC LIMIT #{offset}, #{pageSize}")
        List<MoodShareVO> findAllWithPageForAdmin(@Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计所有分享总数（管理员，包含已删除）
         */
        @Select("SELECT COUNT(*) FROM mood_share")
        Long countAllForAdmin();

        /**
         * 更新删除状态（管理员）
         */
        @Update("UPDATE mood_share SET is_deleted = #{isDeleted} WHERE id = #{id}")
        int updateDeleteStatus(@Param("id") Long id, @Param("isDeleted") Integer isDeleted);
}
