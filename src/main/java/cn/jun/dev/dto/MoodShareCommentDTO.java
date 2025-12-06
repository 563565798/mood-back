package cn.jun.dev.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MoodShareCommentDTO {
    @NotNull(message = "分享ID不能为空")
    private Long shareId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;

    /** 是否匿名 */
    private Boolean isAnonymous;
}
