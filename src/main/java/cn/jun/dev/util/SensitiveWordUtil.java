package cn.jun.dev.util;

import cn.jun.dev.exception.BusinessException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤工具类
 */
public class SensitiveWordUtil {

    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            // 色情
            "色情", "淫秽", "AV", "苍井空",
            // 暴力/血腥
            "杀人", "暴乱", "血腥", "砍死", "炸弹",
            // 反政治 (示例)
            "反动", "暴政", "独裁",
            // 侮辱性词汇
            "傻逼", "白痴", "弱智"));

    /**
     * 检查文本是否包含敏感词
     * 
     * @param text 待检查文本
     * @throws BusinessException 如果包含敏感词
     */
    public static void validate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        for (String word : SENSITIVE_WORDS) {
            if (text.contains(word)) {
                throw new BusinessException("内容包含敏感词：" + word + "，请修改后重试");
            }
        }
    }

    /**
     * 添加敏感词（运行时）
     */
    public static void addWord(String word) {
        SENSITIVE_WORDS.add(word);
    }
}
