package cn.jun.dev.controller;

import cn.jun.dev.common.PageResult;
import cn.jun.dev.common.Result;
import cn.jun.dev.dto.MoodShareDTO;
import cn.jun.dev.service.MoodShareService;
import cn.jun.dev.vo.MoodShareVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 心情分享墙控制器
 */
@Api(tags = "心情分享墙")
@RestController
@RequestMapping("/mood-shares")
public class MoodShareController {
    
    @Autowired
    private MoodShareService moodShareService;
    
    @ApiOperation("创建分享")
    @PostMapping
    public Result<Void> createShare(@Valid @RequestBody MoodShareDTO dto) {
        moodShareService.createShare(dto);
        return Result.success("分享成功", null);
    }
    
    @ApiOperation("删除分享")
    @DeleteMapping("/{id}")
    public Result<Void> deleteShare(@PathVariable Long id) {
        moodShareService.deleteShare(id);
        return Result.success("删除成功", null);
    }
    
    @ApiOperation("分页查询分享列表")
    @GetMapping
    public Result<PageResult<MoodShareVO>> getSharePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<MoodShareVO> pageResult = moodShareService.getSharePage(pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    @ApiOperation("点赞/取消点赞")
    @PostMapping("/{id}/like")
    public Result<Void> toggleLike(@PathVariable Long id) {
        moodShareService.toggleLike(id);
        return Result.success("操作成功", null);
    }
}



