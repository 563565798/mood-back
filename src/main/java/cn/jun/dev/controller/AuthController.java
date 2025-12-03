package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.dto.LoginDTO;
import cn.jun.dev.dto.RegisterDTO;
import cn.jun.dev.service.AuthService;
import cn.jun.dev.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success("登录成功", loginVO);
    }

    @ApiOperation("忘记密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody cn.jun.dev.dto.ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return Result.success("密码重置成功", null);
    }
}
