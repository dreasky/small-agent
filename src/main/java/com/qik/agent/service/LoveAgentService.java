package com.qik.agent.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * @author : Qik 2025/8/27 12:59
 */
public interface LoveAgentService {

    /**
     * 基础对话
     *
     * @param chatId      会话id
     * @param userMessage 用户消息
     * @return 回复消息
     */
    String doChat(String chatId, String userMessage);

    /**
     * 流式对话
     *
     * @param chatId      会话id
     * @param userMessage 用户消息
     * @return 回复消息
     */
    Flux<ServerSentEvent<String>> doChatByStream(String chatId, String userMessage);

    /**
     * 带知识库的对话
     *
     * @param chatId      会话id
     * @param userMessage 用户消息
     * @return 回复消息
     */
    String doChatWithRAG(String chatId, String userMessage);
}
