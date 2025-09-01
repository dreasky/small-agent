package com.qik.agent.service.impl;

import com.qik.agent.service.LoveAgentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


/**
 * @author : Qik 2025/7/30 19:44
 */
@Service
@Slf4j
public class LoveAgent implements LoveAgentService {

    /**
     * 向量储存
     */
    @Resource
    private VectorStore pgVectorStore;

    /**
     * 会话角色
     */
    @Resource
    private ChatClient loveChatClient;


    @Override
    public String doChat(String chatId, String userMessage) {
        return loveChatClient.prompt()
                .user(userMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }

    @Override
    public Flux<ServerSentEvent<String>> doChatByStream(String chatId, String userMessage) {
        return loveChatClient.prompt()
                .user(userMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build()
                );
    }

    @Override
    public String doChatWithRAG(String chatId, String userMessage) {
        return loveChatClient.prompt()
                .user(userMessage)
                .advisors(QuestionAnswerAdvisor.builder(pgVectorStore).build())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
