package com.qik.agent.ai.query;

import com.qik.agent.utility.translation.TencentTranslation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;

/**
 * 用户查询转换器工厂
 *
 * @author : Qik 2025/8/16 17:25
 */
public class QueryTransformerFactory {

    /**
     * 创建将用户提问重写的查询转换器
     *
     * @param chatModel 会话模型
     * @return 查询转换器
     */
    public static QueryTransformer creatRewrite(ChatModel chatModel) {
        return RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(chatModel))
                .build();
    }

    /**
     * 创建将用户提问翻译的查询转换器，针对大模型最擅长的语言做优化
     *
     * @param translation 腾讯云翻译器
     * @return 查询转换器
     */
    public static QueryTransformer creatTranslation(TencentTranslation translation) {
        return query -> {
            String text = query.text();
            String translate = translation.translate(text);
            return new Query(translate);
        };
    }
}
