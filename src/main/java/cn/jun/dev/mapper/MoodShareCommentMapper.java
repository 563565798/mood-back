package cn.jun.dev.mapper;

import cn.jun.dev.entity.MoodShareComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MoodShareCommentMapper {

    @Insert("INSERT INTO mood_share_comment(share_id, user_id, content, anonymous_name, parent_id, created_at) " +
            "VALUES(#{shareId}, #{userId}, #{content}, #{anonymousName}, #{parentId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MoodShareComment comment);

    @Update("UPDATE mood_share_comment SET is_deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM mood_share_comment WHERE id = #{id} AND is_deleted = 0")
    MoodShareComment findById(Long id);

    @Select("SELECT * FROM mood_share_comment WHERE share_id = #{shareId} AND is_deleted = 0 ORDER BY created_at ASC")
    List<MoodShareComment> findByShareId(Long shareId);

    @Select("SELECT * FROM mood_share_comment WHERE share_id = #{shareId} AND user_id = #{userId} LIMIT 1")
    MoodShareComment findByShareIdAndUserId(@Param("shareId") Long shareId, @Param("userId") Long userId);
}
