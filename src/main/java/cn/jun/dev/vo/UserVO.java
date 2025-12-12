package cn.jun.dev.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String email;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private Integer status;

    private Integer role;

    private Integer isMsgOpen;

    private LocalDateTime createdAt;
}
