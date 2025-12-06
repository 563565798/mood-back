package cn.jun.dev.service;

import cn.jun.dev.dto.SendMessageDTO;
import cn.jun.dev.entity.MoodShare;
import cn.jun.dev.entity.MoodShareComment;
import cn.jun.dev.entity.User;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.MoodShareCommentMapper;
import cn.jun.dev.mapper.MoodShareMapper;
import cn.jun.dev.mapper.UserMapper;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务
 */
@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MoodShareMapper moodShareMapper;

    @Autowired
    private MoodShareCommentMapper moodShareCommentMapper;

    @Autowired
    private MessageService messageService;

    /**
     * 分页获取用户列表
     */
    public PageVO<AdminUserVO> getUserList(Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<User> users = userMapper.findAllWithPage(offset, size);
        Long total = userMapper.countAll();

        List<AdminUserVO> voList = users.stream().map(user -> {
            AdminUserVO vo = new AdminUserVO();
            BeanUtils.copyProperties(user, vo);
            // 获取统计数据
            vo.setShareCount(moodShareMapper.countByUserId(user.getId()));
            return vo;
        }).collect(Collectors.toList());

        PageVO<AdminUserVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 不允许禁用管理员
        if (user.getRole() != null && user.getRole() == 1) {
            throw new BusinessException("不能禁用管理员账户");
        }
        userMapper.updateStatus(userId, status);
    }

    /**
     * 重置用户头像为默认
     */
    public void resetUserAvatar(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateAvatar(userId, null);

        // 发送违规通知私信
        Long currentAdminId = SecurityUtil.getCurrentUserId();
        // 避免自己给自己发私信报错
        if (!currentAdminId.equals(userId)) {
            cn.jun.dev.dto.SendMessageDTO messageDTO = new SendMessageDTO();
            messageDTO.setReceiverId(userId);
            messageDTO.setContent("您的头像因违规已被重置为默认头像，请注意遵守社区规范。");
            messageService.sendMessage(currentAdminId, messageDTO);
        }
    }

    /**
     * 分页获取帖子列表（包含已删除）
     */
    public PageVO<AdminShareVO> getShareList(Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MoodShareVO> shares = moodShareMapper.findAllWithPageForAdmin(offset, size);
        Long total = moodShareMapper.countAllForAdmin();

        List<AdminShareVO> voList = shares.stream().map(share -> {
            AdminShareVO vo = new AdminShareVO();
            BeanUtils.copyProperties(share, vo);
            vo.setMoodType(share.getMoodType());
            return vo;
        }).collect(Collectors.toList());

        PageVO<AdminShareVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

    /**
     * 更新帖子删除状态
     */
    public void updateShareDeleteStatus(Long shareId, Integer isDeleted) {
        MoodShare share = moodShareMapper.findById(shareId);
        if (share == null && isDeleted == 1) {
            throw new BusinessException("帖子不存在");
        }
        moodShareMapper.updateDeleteStatus(shareId, isDeleted);
    }

    /**
     * 分页获取评论列表（包含已删除）
     */
    public PageVO<AdminCommentVO> getCommentList(Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MoodShareComment> comments = moodShareCommentMapper.findAllWithPageForAdmin(offset, size);
        Long total = moodShareCommentMapper.countAllForAdmin();

        List<AdminCommentVO> voList = comments.stream().map(comment -> {
            AdminCommentVO vo = new AdminCommentVO();
            BeanUtils.copyProperties(comment, vo);
            return vo;
        }).collect(Collectors.toList());

        PageVO<AdminCommentVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        return pageVO;
    }

    /**
     * 更新评论删除状态
     */
    public void updateCommentDeleteStatus(Long commentId, Integer isDeleted) {
        MoodShareComment comment = moodShareCommentMapper.findById(commentId);
        if (comment == null && isDeleted == 1) {
            throw new BusinessException("评论不存在");
        }
        moodShareCommentMapper.updateDeleteStatus(commentId, isDeleted);
    }

    /**
     * 获取统计数据
     */
    public AdminStatsVO getStats() {
        AdminStatsVO stats = new AdminStatsVO();
        stats.setTotalUsers(userMapper.countAll());
        stats.setTotalShares(moodShareMapper.countAllForAdmin());
        stats.setTotalComments(moodShareCommentMapper.countAllForAdmin());
        // 被删除数量需要额外查询，这里简化处理
        stats.setDeletedShares(0L);
        stats.setDeletedComments(0L);
        stats.setTodayNewUsers(0L);
        stats.setTodayNewShares(0L);
        stats.setTodayNewComments(0L);
        return stats;
    }
}
