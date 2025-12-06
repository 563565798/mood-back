package cn.jun.dev.service;

import cn.jun.dev.dto.SendMessageDTO;
import cn.jun.dev.entity.PrivateMessage;
import cn.jun.dev.entity.User;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.PrivateMessageMapper;
import cn.jun.dev.mapper.UserMapper;
import cn.jun.dev.vo.MessageVO;
import cn.jun.dev.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 私信服务
 */
@Service
public class MessageService {

    @Autowired
    private PrivateMessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发送私信
     */
    public void sendMessage(Long senderId, SendMessageDTO dto) {
        // 检查接收者是否存在
        User receiver = userMapper.findById(dto.getReceiverId());
        if (receiver == null) {
            throw new BusinessException("接收者不存在");
        }

        // 不能给自己发私信
        if (senderId.equals(dto.getReceiverId())) {
            throw new BusinessException("不能给自己发送私信");
        }

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());

        messageMapper.insert(message);
    }

    /**
     * 获取收件箱
     */
    public PageVO<MessageVO> getInbox(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MessageVO> list = messageMapper.findInbox(userId, offset, size);
        Long total = messageMapper.countInbox(userId);

        PageVO<MessageVO> pageVO = new PageVO<>();
        pageVO.setList(list);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

    /**
     * 获取发件箱
     */
    public PageVO<MessageVO> getOutbox(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MessageVO> list = messageMapper.findOutbox(userId, offset, size);
        Long total = messageMapper.countOutbox(userId);

        PageVO<MessageVO> pageVO = new PageVO<>();
        pageVO.setList(list);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

    /**
     * 标记消息为已读
     */
    public void markAsRead(Long messageId, Long userId) {
        messageMapper.markAsRead(messageId, userId);
    }

    /**
     * 标记所有消息为已读
     */
    public void markAllAsRead(Long userId) {
        messageMapper.markAllAsRead(userId);
    }

    /**
     * 获取未读消息数
     */
    public Long getUnreadCount(Long userId) {
        return messageMapper.countUnread(userId);
    }
}
