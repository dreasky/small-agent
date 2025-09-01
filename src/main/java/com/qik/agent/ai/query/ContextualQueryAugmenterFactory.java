package com.qik.agent.ai.query;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建自定义的上下文查询增强器的工厂
 *
 * @author : Qik 2025/8/29 20:23
 */
public class ContextualQueryAugmenterFactory {

    /**
     * 创建love应用的上下文查询增强器
     *
     * @return 上下文查询增强器
     */
    public static ContextualQueryAugmenter bookmarkAugmenter() {
        // 自定义查询为空是模板
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能整理书签数据，
                有问题可以联系我们：qq89342041
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
