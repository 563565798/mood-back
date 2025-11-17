package cn.jun.dev.mapper;

import org.apache.ibatis.annotations.*;

/**
 * 分享点赞Mapper
 */
@Mapper
public interface MoodShareLikeMapper {
    
    /**
     * 插入点赞记录
     */
    @Insert("INSERT INTO mood_share_like(share_id, user_id, created_at) " +
            "VALUES(#{shareId}, #{userId}, NOW())")
    int insert(@Param("shareId") Long shareId, @Param("userId") Long userId);
    
    /**
     * 删除点赞记录
     */
    @Delete("DELETE FROM mood_share_like WHERE share_id = #{shareId} AND user_id = #{userId}")
    int delete(@Param("shareId") Long shareId, @Param("userId") Long userId);
    
    /**
     * 检查是否已点赞
     */
    @Select("SELECT COUNT(*) FROM mood_share_like WHERE share_id = #{shareId} AND user_id = #{userId}")
    int existsByShareIdAndUserId(@Param("shareId") Long shareId, @Param("userId") Long userId);
}




