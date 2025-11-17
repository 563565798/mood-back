package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.entity.MoodType;
import cn.jun.dev.service.MoodTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 情绪类型控制器
 */
@Api(tags = "情绪类型")
@RestController
@RequestMapping("/mood-types")
public class MoodTypeController {
    
    @Autowired
    private MoodTypeService moodTypeService;
    
    @ApiOperation("获取所有情绪类型")
    @GetMapping
    public Result<List<MoodType>> getAllMoodTypes() {
        List<MoodType> moodTypes = moodTypeService.getAllMoodTypes();
        return Result.success(moodTypes);
    }
    
    @ApiOperation("根据分类获取情绪类型")
    @GetMapping("/category")
    public Result<List<MoodType>> getMoodTypesByCategory(@RequestParam String category) {
        List<MoodType> moodTypes = moodTypeService.getMoodTypesByCategory(category);
        return Result.success(moodTypes);
    }
}



