package cn.jun.dev.mapper;

import cn.jun.dev.entity.MoodType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 情绪类型Mapper
 */
@Mapper
public interface MoodTypeMapper {
    
    /**
     * 查询所有情绪类型
     */
    @Select("SELECT * FROM mood_type ORDER BY sort_order ASC")
    List<MoodType> findAll();
    
    /**
     * 根据ID查询情绪类型
     */
    @Select("SELECT * FROM mood_type WHERE id = #{id}")
    MoodType findById(Long id);
    
    /**
     * 根据分类查询情绪类型
     */
    @Select("SELECT * FROM mood_type WHERE category = #{category} ORDER BY sort_order ASC")
    List<MoodType> findByCategory(String category);
}




