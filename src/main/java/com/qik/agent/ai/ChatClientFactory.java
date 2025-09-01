package com.qik.agent.ai;

import com.qik.agent.ai.advisor.BatchProcessingAdvisor;
import com.qik.agent.ai.advisor.MyLoggerAdvisor;
import com.qik.agent.ai.advisor.SensitiveWordAdvisor;
import com.qik.agent.model.love.UserInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * 会话智能体工厂
 *
 * @author : Qik 2025/8/15 22:39
 */
@Configuration
public class ChatClientFactory {

    /**
     * 书签管理助手
     *
     * @param systemResource     系统提示词
     * @param dashscopeChatModel 大模型
     * @return 智能体
     */
    @Bean
    public ChatClient bookmarkChatClient(
            @Value("classpath:prompts/system-bookmark-agent.st") Resource systemResource,
            ChatModel dashscopeChatModel
    ) {
        // advisor 列表
        List<Advisor> advisors = List.of(
                // Token统计 Ordered.LOWEST_PRECEDENCE - 8000
                BatchProcessingAdvisor.builder().build(),
                // 日志输出 Ordered.LOWEST_PRECEDENCE - 1000
                MyLoggerAdvisor.builder().build()
        );

        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemResource)
                .defaultAdvisors(advisors)
                .build();
    }

    /**
     * 大姐姐恋爱助手
     *
     * @param systemResource     系统提示词
     * @param dashscopeChatModel 大模型
     * @param redisChatMemory    会话记忆
     * @return 智能体
     */
    @Bean
    public ChatClient loveChatClient(
            @Value("classpath:prompts/system-love-agent.st") Resource systemResource,
            ChatModel dashscopeChatModel,
            ChatMemory redisChatMemory
    ) {
        // todo 临时硬编码的用户信息
        Map<String, Object> userinfo = new UserInfo("林檬", "男").userInfo();

        // advisor 列表
        List<Advisor> advisors = List.of(
                // 会话记忆顾问 Ordered.HIGHEST_PRECEDENCE + 1000
                MessageChatMemoryAdvisor.builder(redisChatMemory).build(),
                // 敏感词过滤 Ordered.HIGHEST_PRECEDENCE + 2000
                SensitiveWordAdvisor.builder().build(),
                // 日志输出 Ordered.LOWEST_PRECEDENCE - 1000
                MyLoggerAdvisor.builder().build()
        );

        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(spec -> spec.text(systemResource).params(userinfo))
                .defaultAdvisors(advisors)
                .build();
    }
}
