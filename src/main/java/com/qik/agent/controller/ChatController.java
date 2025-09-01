package com.qik.agent.controller;

import com.qik.agent.service.LoveAgentService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author : Qik 2025/7/30 19:08
 */
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Resource
    private LoveAgentService loveAgent;

    @GetMapping("/chat/sync")
    public String chat(String chatId, String userMessage) {
        return loveAgent.doChat(chatId, userMessage);
    }

    @GetMapping("/chat/sse")
    public Flux<ServerSentEvent<String>> chatWithSSE(String chatId, String userMessage) {
        return this.loveAgent.doChatByStream(chatId, userMessage);
    }

    @GetMapping("/chat/rag")
    public String chatWithRAG(String chatId, String userMessage) {
        return loveAgent.doChatWithRAG(chatId, userMessage);
    }
}
