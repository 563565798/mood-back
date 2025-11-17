package cn.jun.dev.service;

import cn.jun.dev.common.PageResult;
import cn.jun.dev.common.ResultCode;
import cn.jun.dev.dto.MoodShareDTO;
import cn.jun.dev.entity.MoodShare;
import cn.jun.dev.exception.BusinessException;
import cn.jun.dev.mapper.MoodShareLikeMapper;
import cn.jun.dev.mapper.MoodShareMapper;
import cn.jun.dev.mapper.MoodTypeMapper;
import cn.jun.dev.util.SecurityUtil;
import cn.jun.dev.vo.MoodShareVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * 心情分享服务
 */
@Service
public class MoodShareService {
    
    @Autowired
    private MoodShareMapper moodShareMapper;
    
    @Autowired
    private MoodShareLikeMapper moodShareLikeMapper;
    
    @Autowired
    private MoodTypeMapper moodTypeMapper;
    
    private static final String[] ANONYMOUS_NAMES = {
        "路过的云", "微风", "星星", "月亮", "太阳", "雨滴", "雪花", "彩虹",
        "蒲公英", "向日葵", "小草", "大树", "小鸟", "蝴蝶", "蜜蜂", "萤火虫"
    };
    
    /**
     * 创建分享
     */
    public void createShare(MoodShareDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 验证情绪类型
        if (moodTypeMapper.findById(dto.getMoodTypeId()) == null) {
            throw new BusinessException(ResultCode.MOOD_TYPE_NOT_FOUND);
        }
        
        MoodShare share = new MoodShare();
        BeanUtils.copyProperties(dto, share);
        share.setUserId(userId);
        
        // 生成随机匿名昵称
        if (share.getAnonymousName() == null || share.getAnonymousName().isEmpty()) {
            share.setAnonymousName(generateAnonymousName());
        }
        
        share.setLikeCount(0);
        share.setCommentCount(0);
        share.setIsDeleted(0);
        
        moodShareMapper.insert(share);
    }
    
    /**
     * 删除分享
     */
    public void deleteShare(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        MoodShare share = moodShareMapper.findById(id);
        if (share == null) {
            throw new BusinessException(ResultCode.MOOD_SHARE_NOT_FOUND);
        }
        
        // 验证权限
        if (!share.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        moodShareMapper.deleteById(id);
    }
    
    /**
     * 分页查询分享列表
     */
    public PageResult<MoodShareVO> getSharePage(Integer pageNum, Integer pageSize) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        int offset = (pageNum - 1) * pageSize;
        List<MoodShareVO> shares = moodShareMapper.findAllWithPage(offset, pageSize);
        
        // 设置是否已点赞
        for (MoodShareVO share : shares) {
            boolean isLiked = moodShareLikeMapper.existsByShareIdAndUserId(share.getId(), currentUserId) > 0;
            share.setIsLiked(isLiked);
        }
        
        Long total = moodShareMapper.countAll();
        
        return new PageResult<>(shares, total, pageNum, pageSize);
    }
    
    /**
     * 点赞/取消点赞
     */
    @Transactional
    public void toggleLike(Long shareId) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        MoodShare share = moodShareMapper.findById(shareId);
        if (share == null) {
            throw new BusinessException(ResultCode.MOOD_SHARE_NOT_FOUND);
        }
        
        // 检查是否已点赞
        boolean isLiked = moodShareLikeMapper.existsByShareIdAndUserId(shareId, userId) > 0;
        
        if (isLiked) {
            // 取消点赞
            moodShareLikeMapper.delete(shareId, userId);
            moodShareMapper.decrementLikeCount(shareId);
        } else {
            // 点赞
            moodShareLikeMapper.insert(shareId, userId);
            moodShareMapper.incrementLikeCount(shareId);
        }
    }
    
    /**
     * 生成随机匿名昵称
     */
    private String generateAnonymousName() {
        Random random = new Random();
        return ANONYMOUS_NAMES[random.nextInt(ANONYMOUS_NAMES.length)];
    }
}




