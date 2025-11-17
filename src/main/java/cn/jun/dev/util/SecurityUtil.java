package cn.jun.dev.util;

import cn.jun.dev.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security工具类
 */
public class SecurityUtil {
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getUserId() : null;
    }
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getUsername() : null;
    }
    
    /**
     * 获取当前登录用户信息
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }
}




