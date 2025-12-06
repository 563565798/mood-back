package cn.jun.dev.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@ApiModel(description = "情绪记录查询参数")
public class MoodRecordQueryDTO {

    @ApiModelProperty("情绪类型ID")
    private Long moodTypeId;

    @ApiModelProperty("情绪强度")
    private Integer intensity;

    @ApiModelProperty("标签")
    private String tag;

    @ApiModelProperty("开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
