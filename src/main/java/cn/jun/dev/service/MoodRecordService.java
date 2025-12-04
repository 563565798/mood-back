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
    public PageResult<MoodRecordVO> getRecordPage(Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();

        int offset = (pageNum - 1) * pageSize;
        List<MoodRecordVO> records = moodRecordMapper.findByUserIdWithPage(userId, offset, pageSize);
        Long total = moodRecordMapper.countByUserId(userId);

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
        statistics.setTotalRecords(moodRecordMapper.countByUserId(userId));

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

    private String getWeekdayName(int weekday) {
        String[] names = { "", "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        return names[weekday];
    }
}
