package cn.jun.dev.mapper;

import cn.jun.dev.entity.MoodRecord;
import cn.jun.dev.vo.MoodRecordVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 情绪记录Mapper
 */
@Mapper
public interface MoodRecordMapper {

        /**
         * 插入情绪记录
         */
        @Insert("INSERT INTO mood_record(user_id, mood_type_id, intensity, trigger_event, thoughts, " +
                        "location, weather, tags, images, is_private, record_date, record_time, created_at, updated_at) "
                        +
                        "VALUES(#{userId}, #{moodTypeId}, #{intensity}, #{triggerEvent}, #{thoughts}, " +
                        "#{location}, #{weather}, #{tags}, #{images}, #{isPrivate}, #{recordDate}, #{recordTime}, NOW(), NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(MoodRecord record);

        /**
         * 更新情绪记录
         */
        @Update("UPDATE mood_record SET mood_type_id = #{moodTypeId}, intensity = #{intensity}, " +
                        "trigger_event = #{triggerEvent}, thoughts = #{thoughts}, location = #{location}, " +
                        "weather = #{weather}, tags = #{tags}, images = #{images}, is_private = #{isPrivate}, " +
                        "record_date = #{recordDate}, record_time = #{recordTime}, updated_at = NOW() WHERE id = #{id}")
        int update(MoodRecord record);

        /**
         * 删除情绪记录
         */
        @Delete("DELETE FROM mood_record WHERE id = #{id}")
        int deleteById(Long id);

        /**
         * 根据ID查询情绪记录
         */
        @Select("SELECT * FROM mood_record WHERE id = #{id}")
        MoodRecord findById(Long id);

        /**
         * 分页查询用户的情绪记录（带情绪类型信息）
         */
        @Results(id = "moodRecordWithType", value = {
                        @Result(property = "id", column = "id"),
                        @Result(property = "moodType", column = "mood_type_id", one = @One(select = "cn.jun.dev.mapper.MoodTypeMapper.findById"))
        })
        @Select("SELECT * FROM mood_record WHERE user_id = #{userId} " +
                        "ORDER BY record_date DESC, record_time DESC LIMIT #{offset}, #{pageSize}")
        List<MoodRecordVO> findByUserIdWithPage(@Param("userId") Long userId,
                        @Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计用户的情绪记录总数
         */
        @Select("SELECT COUNT(*) FROM mood_record WHERE user_id = #{userId}")
        Long countByUserId(Long userId);

        /**
         * 查询用户指定日期范围的情绪记录
         */
        @ResultMap("moodRecordWithType")
        @Select("SELECT * FROM mood_record WHERE user_id = #{userId} " +
                        "AND record_date BETWEEN #{startDate} AND #{endDate} " +
                        "ORDER BY record_date DESC, record_time DESC")
        List<MoodRecordVO> findByUserIdAndDateRange(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 统计情绪分布
         */
        @Select("SELECT mt.name as moodName, COUNT(*) as count " +
                        "FROM mood_record mr " +
                        "JOIN mood_type mt ON mr.mood_type_id = mt.id " +
                        "WHERE mr.user_id = #{userId} AND mr.record_date BETWEEN #{startDate} AND #{endDate} " +
                        "GROUP BY mt.name")
        List<Map<String, Object>> countMoodDistribution(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 统计情绪趋势
         */
        @Select("SELECT record_date, AVG(intensity) as avgIntensity " +
                        "FROM mood_record " +
                        "WHERE user_id = #{userId} AND record_date BETWEEN #{startDate} AND #{endDate} " +
                        "GROUP BY record_date " +
                        "ORDER BY record_date")
        List<Map<String, Object>> countMoodTrend(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 统计一周情绪分布
         */
        @Select("SELECT DAYOFWEEK(record_date) as weekday, AVG(intensity) as avgIntensity " +
                        "FROM mood_record " +
                        "WHERE user_id = #{userId} AND record_date BETWEEN #{startDate} AND #{endDate} " +
                        "GROUP BY DAYOFWEEK(record_date)")
        List<Map<String, Object>> countWeekdayMood(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 统计正负面情绪比例
         */
        @Select("SELECT mt.category, COUNT(*) as count " +
                        "FROM mood_record mr " +
                        "JOIN mood_type mt ON mr.mood_type_id = mt.id " +
                        "WHERE mr.user_id = #{userId} AND mr.record_date BETWEEN #{startDate} AND #{endDate} " +
                        "GROUP BY mt.category")
        List<Map<String, Object>> countMoodRatio(@Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * 查询用户所有记录日期(去重,倒序)
         */
        @Select("SELECT DISTINCT record_date FROM mood_record " +
                        "WHERE user_id = #{userId} " +
                        "ORDER BY record_date DESC")
        List<LocalDate> findDistinctRecordDatesByUserId(Long userId);

        /**
         * 查询最近N条记录（带情绪类型）
         */
        @ResultMap("moodRecordWithType")
        @Select("SELECT * FROM mood_record WHERE user_id = #{userId} " +
                        "ORDER BY record_date DESC, record_time DESC LIMIT #{limit}")
        List<MoodRecordVO> findRecentRecords(@Param("userId") Long userId, @Param("limit") Integer limit);
}
