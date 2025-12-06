package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.service.AdminService;
import cn.jun.dev.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@Api(tags = "管理员管理")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    public Result<PageVO<AdminUserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getUserList(page, size));
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("更新成功", null);
    }

    @ApiOperation("重置用户头像")
    @PutMapping("/users/{id}/reset-avatar")
    public Result<Void> resetUserAvatar(@PathVariable Long id) {
        adminService.resetUserAvatar(id);
        return Result.success("头像已重置为默认", null);
    }

    @ApiOperation("获取帖子列表")
    @GetMapping("/shares")
    public Result<PageVO<AdminShareVO>> getShareList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getShareList(page, size));
    }

    @ApiOperation("更新帖子删除状态")
    @PutMapping("/shares/{id}/delete-status")
    public Result<Void> updateShareDeleteStatus(
            @PathVariable Long id,
            @RequestParam Integer isDeleted) {
        adminService.updateShareDeleteStatus(id, isDeleted);
        return Result.success(isDeleted == 1 ? "删除成功" : "恢复成功", null);
    }

    @ApiOperation("获取评论列表")
    @GetMapping("/comments")
    public Result<PageVO<AdminCommentVO>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getCommentList(page, size));
    }

    @ApiOperation("更新评论删除状态")
    @PutMapping("/comments/{id}/delete-status")
    public Result<Void> updateCommentDeleteStatus(
            @PathVariable Long id,
            @RequestParam Integer isDeleted) {
        adminService.updateCommentDeleteStatus(id, isDeleted);
        return Result.success(isDeleted == 1 ? "删除成功" : "恢复成功", null);
    }

    @ApiOperation("获取统计数据")
    @GetMapping("/stats")
    public Result<AdminStatsVO> getStats() {
        return Result.success(adminService.getStats());
    }
}
