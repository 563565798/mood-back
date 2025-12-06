package cn.jun.dev.service;

import cn.jun.dev.common.ResultCode;
import cn.jun.dev.dto.UpdateUserDTO;
import cn.jun.dev.entity.User;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.UserMapper;
import cn.jun.dev.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据ID获取用户信息
     */
    public UserVO getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user) {
        userMapper.update(user);
    }

    /**
     * 更新当前用户信息
     */
    public void updateUserInfo(Long userId, UpdateUserDTO dto) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 只更新允许修改的字段
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }

        userMapper.update(user);
    }

    /**
     * 根据用户名获取用户信息
     */
    public UserVO getUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
