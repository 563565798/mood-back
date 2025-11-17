package cn.jun.dev.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    USER_NOT_LOGIN(1005, "用户未登录"),
    
    // 认证相关 2xxx
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    UNAUTHORIZED(2003, "未授权"),
    FORBIDDEN(2004, "无权限"),
    
    // 参数相关 3xxx
    PARAM_ERROR(3001, "参数错误"),
    PARAM_MISSING(3002, "参数缺失"),
    PARAM_INVALID(3003, "参数格式错误"),
    
    // 业务相关 4xxx
    MOOD_RECORD_NOT_FOUND(4001, "情绪记录不存在"),
    MOOD_TYPE_NOT_FOUND(4002, "情绪类型不存在"),
    MOOD_SHARE_NOT_FOUND(4003, "分享不存在"),
    ALREADY_LIKED(4004, "已经点赞过了"),
    
    // 系统相关 5xxx
    SYSTEM_ERROR(5000, "系统错误"),
    DATABASE_ERROR(5001, "数据库错误"),
    REDIS_ERROR(5002, "缓存错误");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}




