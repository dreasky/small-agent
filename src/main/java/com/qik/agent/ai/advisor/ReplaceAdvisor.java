package com.qik.agent.ai.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.core.Ordered;

/**
 * @author : Qik 2025/8/29 17:45
 */
public class ReplaceAdvisor implements BaseAdvisor {
    private final QueryTransformer queryTransformer;

    private final int order;

    private ReplaceAdvisor(Builder builder) {
        this.order = builder.order;
        this.queryTransformer = builder.queryTransformer;
    }

    @NotNull
    @Override
    public ChatClientRequest before(ChatClientRequest request, @NotNull AdvisorChain chain) {
        // 获取替换原用户提示词
        String userText = request.prompt().getUserMessage().getText();
        String newUserText = queryTransformer.transform(new Query(userText)).text();

        // 修改用户提示词
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

    public static Builder builder(QueryTransformer queryTransformer) {
        return new Builder(queryTransformer);
    }

    public static class Builder {
        private int order = Ordered.HIGHEST_PRECEDENCE + 2100;

        private final QueryTransformer queryTransformer;

        private Builder(QueryTransformer queryTransformer) {
            this.queryTransformer = queryTransformer;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public ReplaceAdvisor build() {
            return new ReplaceAdvisor(this);
        }
    }
}
