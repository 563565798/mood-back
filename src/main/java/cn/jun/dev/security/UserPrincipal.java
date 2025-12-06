package cn.jun.dev.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {

    private Long userId;

    private String username;

    private Integer role;
}
