package cn.jun.dev.mapper;

import cn.jun.dev.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 标签Mapper
 */
@Mapper
public interface TagMapper {
    
    /**
     * 查询所有系统标签
     */
    @Select("SELECT * FROM tag WHERE user_id IS NULL ORDER BY usage_count DESC")
    List<Tag> findSystemTags();
    
    /**
     * 查询用户的自定义标签
     */
    @Select("SELECT * FROM tag WHERE user_id = #{userId} ORDER BY usage_count DESC")
    List<Tag> findByUserId(Long userId);
    
    /**
     * 根据名称查询标签
     */
    @Select("SELECT * FROM tag WHERE name = #{name} AND (user_id = #{userId} OR user_id IS NULL)")
    Tag findByName(@Param("name") String name, @Param("userId") Long userId);
    
    /**
     * 插入标签
     */
    @Insert("INSERT INTO tag(name, category, color, user_id, created_at) " +
            "VALUES(#{name}, #{category}, #{color}, #{userId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);
    
    /**
     * 增加使用次数
     */
    @Update("UPDATE tag SET usage_count = usage_count + 1 WHERE id = #{id}")
    int incrementUsageCount(Long id);
}




