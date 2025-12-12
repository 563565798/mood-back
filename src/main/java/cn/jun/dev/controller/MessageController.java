package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.dto.SendMessageDTO;
import cn.jun.dev.service.MessageService;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.MessageVO;
import cn.jun.dev.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 私信控制器
 */
@Api(tags = "私信管理")
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("发送私信")
    @PostMapping
    public Result<Void> sendMessage(@Valid @RequestBody SendMessageDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.sendMessage(userId, dto);
        return Result.success("发送成功", null);
    }

    @ApiOperation("获取收件箱")
    @GetMapping("/inbox")
    public Result<PageVO<MessageVO>> getInbox(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(messageService.getInbox(userId, page, size));
    }

    @ApiOperation("获取发件箱")
    @GetMapping("/outbox")
    public Result<PageVO<MessageVO>> getOutbox(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(messageService.getOutbox(userId, page, size));
    }

    @ApiOperation("标记消息为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.markAsRead(id, userId);
        return Result.success("已标记为已读", null);
    }

    @ApiOperation("标记所有消息为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.markAllAsRead(userId);
        return Result.success("已全部标记为已读", null);
    }

    @ApiOperation("获取未读消息数")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(messageService.getUnreadCount(userId));
    }

    @ApiOperation("删除私信")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.deleteMessage(id, userId);
        return Result.success("删除成功", null);
    }
}
