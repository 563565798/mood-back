package cn.jun.dev.service;

import cn.jun.dev.entity.MoodType;
import cn.jun.dev.mapper.MoodTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 情绪类型服务
 */
@Service
public class MoodTypeService {
    
    @Autowired
    private MoodTypeMapper moodTypeMapper;
    
    /**
     * 获取所有情绪类型
     */
    public List<MoodType> getAllMoodTypes() {
        return moodTypeMapper.findAll();
    }
    
    /**
     * 根据分类获取情绪类型
     */
    public List<MoodType> getMoodTypesByCategory(String category) {
        return moodTypeMapper.findByCategory(category);
    }
}




