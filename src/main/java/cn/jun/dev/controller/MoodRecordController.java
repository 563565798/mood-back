package cn.jun.dev.controller;

import cn.jun.dev.common.PageResult;
import cn.jun.dev.common.Result;
import cn.jun.dev.dto.MoodRecordDTO;
import cn.jun.dev.service.MoodRecordService;
import cn.jun.dev.vo.MoodRecordVO;
import cn.jun.dev.vo.MoodStatisticsVO;
import cn.jun.dev.vo.MoodWarningVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 情绪记录控制器
 */
@Api(tags = "情绪记录")
@RestController
@RequestMapping("/mood-records")
public class MoodRecordController {
    
    @Autowired
    private MoodRecordService moodRecordService;
    
    @ApiOperation("创建情绪记录")
    @PostMapping
    public Result<MoodWarningVO> createRecord(@Valid @RequestBody MoodRecordDTO dto) {
        MoodWarningVO warning = moodRecordService.createRecord(dto);
        return Result.success("创建成功", warning);
    }
    
    @ApiOperation("更新情绪记录")
    @PutMapping("/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @Valid @RequestBody MoodRecordDTO dto) {
        moodRecordService.updateRecord(id, dto);
        return Result.success("更新成功", null);
    }
    
    @ApiOperation("删除情绪记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        moodRecordService.deleteRecord(id);
        return Result.success("删除成功", null);
    }
    
    @ApiOperation("分页查询情绪记录")
    @GetMapping
    public Result<PageResult<MoodRecordVO>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<MoodRecordVO> pageResult = moodRecordService.getRecordPage(pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    @ApiOperation("获取情绪记录详情")
    @GetMapping("/{id}")
    public Result<MoodRecordVO> getRecordDetail(@PathVariable Long id) {
        MoodRecordVO recordVO = moodRecordService.getRecordDetail(id);
        return Result.success(recordVO);
    }
    
    @ApiOperation("获取情绪统计数据")
    @GetMapping("/statistics")
    public Result<MoodStatisticsVO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        MoodStatisticsVO statistics = moodRecordService.getStatistics(startDate, endDate);
        return Result.success(statistics);
    }

    @ApiOperation("获取心情总结")
    @GetMapping("/summary")
    public Result<cn.jun.dev.vo.MoodSummaryVO> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        cn.jun.dev.vo.MoodSummaryVO summary = moodRecordService.getSummary(startDate, endDate);
        return Result.success(summary);
    }
}



