package com.qik.agent.ai.memory;


import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建自定义的会话记忆工厂
 *
 * @author : Qik 2025/8/15 22:26
 */
@Configuration
public class ChatMemoryFactory {

    @Bean
    public ChatMemory redisChatMemory(RedisChatMemoryRepository redisChatMemoryRepository) {
        // 构建带消息窗口的记忆组件，最多保留最近 10 条消息
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(10)
                .build();
    }
}
