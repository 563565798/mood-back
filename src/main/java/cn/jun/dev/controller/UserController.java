package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.dto.UpdateUserDTO;
import cn.jun.dev.service.UserService;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @ApiOperation("获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }
    
    @ApiOperation("更新当前用户信息")
    @PutMapping("/me")
    public Result<Void> updateCurrentUser(@Valid @RequestBody UpdateUserDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.updateUserInfo(userId, dto);
        return Result.success("更新成功", null);
    }
}



