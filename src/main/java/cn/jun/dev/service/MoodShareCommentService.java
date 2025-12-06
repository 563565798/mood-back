package cn.jun.dev.service;

import cn.jun.dev.common.ResultCode;
import cn.jun.dev.dto.MoodShareCommentDTO;
import cn.jun.dev.entity.MoodShare;
import cn.jun.dev.entity.MoodShareComment;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.MoodShareCommentMapper;
import cn.jun.dev.mapper.MoodShareMapper;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.MoodShareCommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MoodShareCommentService {

    @Autowired
    private MoodShareCommentMapper commentMapper;

    @Autowired
    private MoodShareMapper shareMapper;

    /**
     * 发表评论
     */
    @Transactional
    public void addComment(MoodShareCommentDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        MoodShare share = shareMapper.findById(dto.getShareId());
        if (share == null) {
            throw new BusinessException(ResultCode.MOOD_SHARE_NOT_FOUND);
        }

        MoodShareComment comment = new MoodShareComment();
        BeanUtils.copyProperties(dto, comment);
        comment.setUserId(userId);

        // 设置匿名状态
        comment.setIsAnonymous(dto.getIsAnonymous() != null && dto.getIsAnonymous() ? 1 : 0);

        // 匿名评论保持昵称一致性逻辑
        if (comment.getIsAnonymous() == 1) {
            String anonymousName;
            if (share.getUserId().equals(userId)) {
                // 如果是分享作者（楼主），使用分享时的匿名昵称
                anonymousName = share.getAnonymousName();
            } else {
                // 查找当前用户在该分享下的历史评论
                MoodShareComment previousComment = commentMapper.findByShareIdAndUserId(dto.getShareId(), userId);
                if (previousComment != null && previousComment.getAnonymousName() != null) {
                    // 如果评论过，使用之前的昵称
                    anonymousName = previousComment.getAnonymousName();
                } else {
                    // 否则生成新昵称
                    anonymousName = generateAnonymousName();
                }
            }
            comment.setAnonymousName(anonymousName);
        }

        comment.setIsDeleted(0);

        commentMapper.insert(comment);
        shareMapper.incrementCommentCount(share.getId());
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();

        MoodShareComment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        MoodShare share = shareMapper.findById(comment.getShareId());

        // 权限检查：评论作者 OR 分享作者
        boolean isCommentAuthor = comment.getUserId().equals(userId);
        boolean isShareAuthor = share != null && share.getUserId().equals(userId);

        if (!isCommentAuthor && !isShareAuthor) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        commentMapper.deleteById(id);
        shareMapper.decrementCommentCount(comment.getShareId());
    }

    /**
     * 获取评论列表
     */
    public List<MoodShareCommentVO> getComments(Long shareId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        MoodShare share = shareMapper.findById(shareId);
        Long shareAuthorId = (share != null) ? share.getUserId() : null;

        List<MoodShareCommentVO> comments = commentMapper.findByShareId(shareId);

        // Create a map for quick lookup of parent names
        Map<Long, String> commentIdToNameMap = comments.stream()
                .collect(Collectors.toMap(MoodShareCommentVO::getId, vo -> {
                    // 优先使用昵称，匿名则用匿名昵称
                    if (vo.getNickname() != null) {
                        return vo.getNickname();
                    }
                    return vo.getAnonymousName() != null ? vo.getAnonymousName() : "匿名用户";
                }));

        for (MoodShareCommentVO vo : comments) {
            // 设置是否是分享作者（楼主）
            vo.setIsOwner(shareAuthorId != null && vo.getUserId().equals(shareAuthorId));

            // 设置是否有删除权限
            boolean isCommentAuthor = vo.getUserId().equals(currentUserId);
            boolean isShareAuthor = shareAuthorId != null && shareAuthorId.equals(currentUserId);
            vo.setCanDelete(isCommentAuthor || isShareAuthor);

            // 设置回复对象昵称
            if (vo.getParentId() != null) {
                vo.setReplyToName(commentIdToNameMap.get(vo.getParentId()));
            }
        }

        return comments;
    }

    private String generateAnonymousName() {
        String[] adjectives = { "开心的", "忧伤的", "愤怒的", "平静的", "激动的", "焦虑的", "迷茫的", "期待的" };
        String[] nouns = { "考拉", "袋鼠", "企鹅", "熊猫", "狮子", "老虎", "兔子", "猫咪", "小狗", "仓鼠" };
        int adjIndex = (int) (Math.random() * adjectives.length);
        int nounIndex = (int) (Math.random() * nouns.length);
        return adjectives[adjIndex] + nouns[nounIndex];
    }
}
