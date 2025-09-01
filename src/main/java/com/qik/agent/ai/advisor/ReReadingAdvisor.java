package com.qik.agent.ai.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.Ordered;


/**
 * 重复问题拦截器
 *
 * @author : Qik 2025/7/31 21:49
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String RE2_TEMPLATE = """
            %s
            Read the question again: %s
            """;

    private final int order;

    private ReReadingAdvisor(Builder builder) {
        this.order = builder.order;
    }

    @NotNull
    @Override
    public ChatClientRequest before(ChatClientRequest request, @NotNull AdvisorChain chain) {
        String userText = request.prompt().getUserMessage().getText();
        // 添加上下文参数
        request.context().put("re2_input_query", userText);
        // 修改用户提示词
        String newUserText = RE2_TEMPLATE.formatted(userText, userText);
        Prompt newPrompt = request.prompt().augmentUserMessage(newUserText);
        return ChatClientRequest.builder()
                .prompt(newPrompt)
                .context(request.context())
                .build();
    }

    @NotNull
    @Override
    public ChatClientResponse after(@NotNull ChatClientResponse response, @NotNull AdvisorChain chain) {
        return response;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int order = Ordered.HIGHEST_PRECEDENCE + 2200;

        private Builder() {
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public ReReadingAdvisor build() {
            return new ReReadingAdvisor(this);
        }
    }
}
