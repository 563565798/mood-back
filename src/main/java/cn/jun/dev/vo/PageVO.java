package cn.jun.dev.vo;

import lombok.Data;
import java.util.List;

/**
 * 分页VO
 */
@Data
public class PageVO<T> {

    /** 数据列表 */
    private List<T> list;

    /** 总数 */
    private Long total;

    /** 当前页 */
    private Integer page;

    /** 每页大小 */
    private Integer size;
}
