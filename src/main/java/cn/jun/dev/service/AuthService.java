package cn.jun.dev.service;

import cn.jun.dev.common.ResultCode;
import cn.jun.dev.dto.LoginDTO;
import cn.jun.dev.dto.RegisterDTO;
import cn.jun.dev.entity.User;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.UserMapper;
import cn.jun.dev.util.JwtUtil;
import cn.jun.dev.vo.LoginVO;
import cn.jun.dev.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public void register(RegisterDTO dto) {
        // 验证两次密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 检查用户名是否已存在
        if (userMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查邮箱是否已存在
        if (dto.getEmail() != null && userMapper.findByEmail(dto.getEmail()) != null) {
            throw new BusinessException("该邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);

        userMapper.insert(user);
    }

    /**
     * 用户登录
     */
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus().equals(0)) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建用户信息VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return new LoginVO(token, userVO);
    }

    /**
     * 重置密码
     */
    public void resetPassword(cn.jun.dev.dto.ResetPasswordDTO dto) {
        // 1. 根据用户名查询用户
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 验证邮箱是否匹配
        if (!dto.getEmail().equals(user.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_NOT_MATCH);
        }

        // 3. 更新密码
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        userMapper.updatePassword(user.getId(), encodedPassword);
    }
}
