package com.qik.agent.ai.query;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;


/**
 * 创建自定义的多轮查询扩展器工厂
 *
 * @author : Qik 2025/8/29 20:23
 */
public class MultiQueryExpanderFactory {

    /**
     * 基础的3轮轮查询扩展器，将单个查询扩展为3个
     *
     * @param chatModel 会话模型
     * @return 多轮查询扩展器
     */
    public MultiQueryExpander threeQueryExpander(ChatModel chatModel) {
        return MultiQueryExpander.builder()
                .chatClientBuilder(ChatClient.builder(chatModel))
                .numberOfQueries(3)
                .build();
    }
}
