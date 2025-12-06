package cn.jun.dev.service;

import cn.jun.dev.common.PageResult;
import cn.jun.dev.common.ResultCode;
import cn.jun.dev.dto.MoodRecordDTO;
import cn.jun.dev.entity.MoodRecord;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.MoodRecordMapper;
import cn.jun.dev.mapper.MoodTypeMapper;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.MoodRecordVO;
import cn.jun.dev.vo.MoodStatisticsVO;
import cn.jun.dev.vo.MoodWarningVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 情绪记录服务
 */
@Service
public class MoodRecordService {

    @Autowired
    private MoodRecordMapper moodRecordMapper;

    @Autowired
    private MoodTypeMapper moodTypeMapper;

    @Autowired
    private cn.jun.dev.mapper.MoodShareMapper moodShareMapper;

    /**
     * 验证记录时间是否合法（不能超过当前时间）
     */
    private void validateRecordTime(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return;
        }
        LocalDateTime recordDateTime = LocalDateTime.of(date, time);
        if (recordDateTime.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "记录时间不能超过当前时间");
        }
    }

    /**
     * 创建情绪记录
     */
    public MoodWarningVO createRecord(MoodRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 验证情绪类型是否存在
        if (moodTypeMapper.findById(dto.getMoodTypeId()) == null) {
            throw new BusinessException(ResultCode.MOOD_TYPE_NOT_FOUND);
        }

        MoodRecord record = new MoodRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);

        // 设置默认值
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        if (record.getRecordTime() == null) {
            record.setRecordTime(LocalTime.now());
        }

        // 验证时间
        validateRecordTime(record.getRecordDate(), record.getRecordTime());

        if (record.getIsPrivate() == null) {
            record.setIsPrivate(1);
        }

        moodRecordMapper.insert(record);

        return checkMoodWarning(userId);
    }

    private MoodWarningVO checkMoodWarning(Long userId) {
        List<MoodRecordVO> recentRecords = moodRecordMapper.findRecentRecords(userId, 3);
        if (recentRecords.size() < 3) {
            return null;
        }

        boolean allNegative = recentRecords.stream()
                .allMatch(r -> r.getMoodType() != null && "negative".equals(r.getMoodType().getCategory()));

        if (allNegative) {
            MoodWarningVO warning = new MoodWarningVO();
            warning.setHasWarning(true);
            warning.setMessage("检测到您最近情绪持续低落，建议您尝试以下方式调节心情：");
            warning.setSuggestions(Arrays.asList("听听舒缓的音乐", "尝试冥想或深呼吸", "找亲密的朋友聊聊", "出去散步晒晒太阳"));
            return warning;
        }
        return null;
    }

    /**
     * 更新情绪记录
     */
    public void updateRecord(Long id, MoodRecordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        MoodRecord existRecord = moodRecordMapper.findById(id);
        if (existRecord == null) {
            throw new BusinessException(ResultCode.MOOD_RECORD_NOT_FOUND);
        }

        // 验证权限
        if (!existRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        // 验证情绪类型
        if (moodTypeMapper.findById(dto.getMoodTypeId()) == null) {
            throw new BusinessException(ResultCode.MOOD_TYPE_NOT_FOUND);
        }

        // 验证时间
        validateRecordTime(dto.getRecordDate(), dto.getRecordTime());

        MoodRecord record = new MoodRecord();
        BeanUtils.copyProperties(dto, record);
        record.setId(id);

        moodRecordMapper.update(record);
    }

    /**
     * 删除情绪记录
     */
    public void deleteRecord(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();

        MoodRecord existRecord = moodRecordMapper.findById(id);
        if (existRecord == null) {
            throw new BusinessException(ResultCode.MOOD_RECORD_NOT_FOUND);
        }

        // 验证权限
        if (!existRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        moodRecordMapper.deleteById(id);
    }

    /**
     * 分页查询情绪记录
     */
    public PageResult<MoodRecordVO> getRecordPage(Integer pageNum, Integer pageSize,
            cn.jun.dev.dto.MoodRecordQueryDTO query) {
        Long userId = SecurityUtil.getCurrentUserId();

        int offset = (pageNum - 1) * pageSize;
        if (query == null) {
            query = new cn.jun.dev.dto.MoodRecordQueryDTO();
        }
        List<MoodRecordVO> records = moodRecordMapper.findByUserIdWithPage(userId, query, offset, pageSize);
        Long total = moodRecordMapper.countByUserId(userId, query);

        return new PageResult<>(records, total, pageNum, pageSize);
    }

    /**
     * 获取情绪记录详情
     */
    public MoodRecordVO getRecordDetail(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();

        MoodRecord record = moodRecordMapper.findById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.MOOD_RECORD_NOT_FOUND);
        }

        // 验证权限
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        MoodRecordVO vo = new MoodRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setMoodType(moodTypeMapper.findById(record.getMoodTypeId()));

        return vo;
    }

    /**
     * 获取情绪统计数据
     */
    public MoodStatisticsVO getStatistics(LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        MoodStatisticsVO statistics = new MoodStatisticsVO();

        // 总记录数
        statistics.setTotalRecords(moodRecordMapper.countByUserId(userId, new cn.jun.dev.dto.MoodRecordQueryDTO()));

        // 计算连续记录天数
        List<LocalDate> recordDates = moodRecordMapper.findDistinctRecordDatesByUserId(userId);
        int continuousDays = 0;
        if (!recordDates.isEmpty()) {
            LocalDate today = LocalDate.now();
            LocalDate checkDate = recordDates.get(0); // 最近的记录日期

            // 如果最近一次记录不是今天或昨天,则连续天数为0
            if (checkDate.isBefore(today.minusDays(1))) {
                continuousDays = 0;
            } else {
                // 从最近的记录日期开始向前检查连续性
                continuousDays = 1;
                for (int i = 1; i < recordDates.size(); i++) {
                    LocalDate prevDate = recordDates.get(i);
                    LocalDate expectedDate = checkDate.minusDays(1);

                    if (prevDate.equals(expectedDate)) {
                        continuousDays++;
                        checkDate = prevDate;
                    } else {
                        // 遇到断档,停止计数
                        break;
                    }
                }
            }
        }
        statistics.setContinuousDays(continuousDays);

        // 情绪分布
        List<Map<String, Object>> distribution = moodRecordMapper.countMoodDistribution(userId, startDate, endDate);
        Map<String, Integer> moodDistribution = distribution.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("moodName"),
                        m -> ((Number) m.get("count")).intValue()));
        statistics.setMoodDistribution(moodDistribution);

        // 情绪趋势
        List<Map<String, Object>> trend = moodRecordMapper.countMoodTrend(userId, startDate, endDate);
        List<MoodStatisticsVO.MoodTrendItem> moodTrend = trend.stream()
                .map(m -> {
                    MoodStatisticsVO.MoodTrendItem item = new MoodStatisticsVO.MoodTrendItem();
                    item.setDate(m.get("record_date").toString());
                    item.setAvgIntensity(((Number) m.get("avgIntensity")).doubleValue());
                    return item;
                })
                .collect(Collectors.toList());
        statistics.setMoodTrend(moodTrend);

        // 一周情绪分布
        List<Map<String, Object>> weekday = moodRecordMapper.countWeekdayMood(userId, startDate, endDate);
        Map<String, Double> weekdayMood = new java.util.HashMap<String, Double>();
        for (Map<String, Object> m : weekday) {
            String dayName = getWeekdayName(((Number) m.get("weekday")).intValue());
            Double avgIntensity = ((Number) m.get("avgIntensity")).doubleValue();
            weekdayMood.put(dayName, avgIntensity);
        }
        statistics.setWeekdayMood(weekdayMood);

        // 正负面情绪比例
        List<Map<String, Object>> ratio = moodRecordMapper.countMoodRatio(userId, startDate, endDate);
        MoodStatisticsVO.MoodRatioVO moodRatio = new MoodStatisticsVO.MoodRatioVO();
        int total = 0;
        Integer positiveCount = 0;
        Integer negativeCount = 0;
        Integer neutralCount = 0;

        for (Map<String, Object> r : ratio) {
            String category = (String) r.get("category");
            int count = ((Number) r.get("count")).intValue();
            total += count;

            if ("positive".equals(category)) {
                positiveCount = count;
            } else if ("negative".equals(category)) {
                negativeCount = count;
            } else {
                neutralCount = count;
            }
        }

        moodRatio.setPositiveCount(positiveCount);
        moodRatio.setNegativeCount(negativeCount);
        moodRatio.setNeutralCount(neutralCount);

        if (total > 0) {
            moodRatio.setPositiveRatio((double) positiveCount / total * 100);
            moodRatio.setNegativeRatio((double) negativeCount / total * 100);
            moodRatio.setNeutralRatio((double) neutralCount / total * 100);
        }

        statistics.setMoodRatio(moodRatio);

        // 标签统计
        List<MoodRecordVO> records = moodRecordMapper.findByUserIdAndDateRange(userId, startDate, endDate);
        Map<String, Integer> tagStatistics = new java.util.HashMap<>();

        for (MoodRecordVO record : records) {
            String tags = record.getTags();
            if (tags != null && !tags.isEmpty()) {
                String[] tagArray = tags.split(",");
                for (String tag : tagArray) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        tagStatistics.put(trimmedTag, tagStatistics.getOrDefault(trimmedTag, 0) + 1);
                    }
                }
            }
        }

        statistics.setTagStatistics(tagStatistics);

        // 分享数统计
        statistics.setShareCount(moodShareMapper.countByUserId(userId));

        return statistics;
    }

    /**
     * 获取心情总结
     */
    public cn.jun.dev.vo.MoodSummaryVO getSummary(LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(6);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<MoodRecordVO> records = moodRecordMapper.findByUserIdAndDateRange(userId, startDate, endDate);

        cn.jun.dev.vo.MoodSummaryVO summary = new cn.jun.dev.vo.MoodSummaryVO();
        summary.setRecordCount(records.size());

        if (records.isEmpty()) {
            summary.setSummaryText("这段时间还没有记录哦，快去记录一下吧！");
            return summary;
        }

        // 平均强度
        double avgIntensity = records.stream()
                .mapToInt(MoodRecordVO::getIntensity)
                .average()
                .orElse(0.0);
        avgIntensity = Math.round(avgIntensity * 10.0) / 10.0;
        summary.setAvgIntensity(avgIntensity);

        // 主导情绪
        Map<String, Long> moodCounts = records.stream()
                .filter(r -> r.getMoodType() != null)
                .collect(Collectors.groupingBy(r -> r.getMoodType().getName(), Collectors.counting()));

        String dominantMood = moodCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");
        summary.setDominantMood(dominantMood);

        // 关键词
        Map<String, Long> tagCounts = new HashMap<>();
        for (MoodRecordVO r : records) {
            if (r.getTags() != null && !r.getTags().isEmpty()) {
                for (String tag : r.getTags().split(",")) {
                    String t = tag.trim();
                    if (!t.isEmpty()) {
                        tagCounts.put(t, tagCounts.getOrDefault(t, 0L) + 1);
                    }
                }
            }
        }
        String keyword = tagCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无");
        summary.setKeyword(keyword);

        // 生成总结文本
        StringBuilder sb = new StringBuilder();
        sb.append("在这段时间里，你共记录了").append(summary.getRecordCount()).append("次心情。");
        sb.append("主导情绪是“").append(dominantMood).append("”，");
        sb.append("平均强度为").append(avgIntensity).append("。");

        if (!"无".equals(keyword)) {
            sb.append("最常提到的关键词是“").append(keyword).append("”。");
        }

        // 根据主导情绪生成建议
        String dominantCategory = records.stream()
                .filter(r -> r.getMoodType() != null && r.getMoodType().getName().equals(dominantMood))
                .map(r -> r.getMoodType().getCategory())
                .findFirst()
                .orElse("neutral");

        if ("positive".equals(dominantCategory)) {
            sb.append("看来这段时间你过得很不错，继续保持哦！");
        } else if ("negative".equals(dominantCategory)) {
            sb.append("最近似乎有些低落，记得多关爱自己，一切都会好起来的。");
        } else {
            sb.append("心情比较平稳，平平淡淡才是真。");
        }

        summary.setSummaryText(sb.toString());

        return summary;
    }

    private String getWeekdayName(int weekday) {
        String[] names = { "", "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        return names[weekday];
    }
}
