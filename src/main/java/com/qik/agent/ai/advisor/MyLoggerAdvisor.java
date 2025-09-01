package com.qik.agent.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.core.Ordered;


/**
 * 自定义日志 Advisor
 * * 打印 info 级别日志
 *
 * @author : Qik 2025/7/31 21:49
 */
@Slf4j
public class MyLoggerAdvisor implements BaseAdvisor {

    private final int order;

    public MyLoggerAdvisor(Builder builder) {
        this.order = builder.order;
    }

    @NotNull
    @Override
    public ChatClientRequest before(ChatClientRequest request, @NotNull AdvisorChain chain) {
        log.info("Ai Request: {}", request.prompt());
        return request;
    }

    @NotNull
    @Override
    public ChatClientResponse after(ChatClientResponse response, @NotNull AdvisorChain chain) {
        log.info("AI Response: {}", response.chatResponse());
        return response;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int order = Ordered.LOWEST_PRECEDENCE - 9000;

        private Builder() {
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public MyLoggerAdvisor build() {
            return new MyLoggerAdvisor(this);
        }
    }
}
