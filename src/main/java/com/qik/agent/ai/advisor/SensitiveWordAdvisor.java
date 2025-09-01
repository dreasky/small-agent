package com.qik.agent.ai.advisor;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.dfa.WordTree;
import com.qik.agent.common.exception.BusinessException;
import com.qik.agent.common.exception.ErrorCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.List;

/**
 * @author : Qik 2025/8/5 15:08
 */
public class SensitiveWordAdvisor implements BaseAdvisor {

    private final int order;

    private final WordMatcher sensitiveWordMatcher;

    public SensitiveWordAdvisor(Builder builder) {
        this.order = builder.order;
        this.sensitiveWordMatcher = builder.sensitiveWordMatcher;
    }

    @NotNull
    @Override
    public ChatClientRequest before(ChatClientRequest request, @NotNull AdvisorChain chain) {
        String text = request.prompt().getUserMessage().getText();
        List<String> strings = sensitiveWordMatcher.matchAll(text);
        // 有敏感词时抛出异常
        if (!strings.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户消息包含敏感词: " + strings);
        }
        return request;
    }

    @NotNull
    @Override
    public ChatClientResponse after(ChatClientResponse response, @NotNull AdvisorChain chain) {
        ChatResponse resp = response.chatResponse();
        if (resp == null) {
            throw new RuntimeException("ai返回结果异常");
        }

        String text = resp.getResult().getOutput().getText();
        List<String> strings = sensitiveWordMatcher.matchAll(text);
        // Ai回复消息包含敏感词
        if (!strings.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "消息包含敏感词: " + strings);
        }
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

        private int order = Ordered.HIGHEST_PRECEDENCE + 2000;

        private WordMatcher sensitiveWordMatcher = new WordMatcher();

        private Builder() {
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder sensitiveWordFilter(WordMatcher sensitiveWordMatcher) {
            this.sensitiveWordMatcher = sensitiveWordMatcher;
            return this;
        }

        public SensitiveWordAdvisor build() {
            return new SensitiveWordAdvisor(this);
        }
    }

    /**
     * 基于词典树的词过滤
     */
    public static class WordMatcher {

        private final WordTree sensitiveWordTree;

        /**
         * 构造函数，默认从文件读取敏感词
         */
        public WordMatcher() {
            this.sensitiveWordTree = new WordTree();
            FileReader fileReader = new FileReader("test_sensitive_dict.txt");
            fileReader.readLines().forEach(sensitiveWordTree::addWord);
        }

        /**
         * 构造函数，自定义导入敏感词列表
         *
         * @param sensitiveWords 敏感词列表
         */
        public WordMatcher(Collection<String> sensitiveWords) {
            this.sensitiveWordTree = new WordTree();
            sensitiveWords.forEach(sensitiveWordTree::addWord);
        }

        /**
         * 是否包含敏感词
         *
         * @param text 待检查的文本
         * @return 是否包含敏感词
         */
        public boolean isMatch(String text) {
            return sensitiveWordTree.isMatch(text);
        }

        /**
         * 获取第一个匹配到的敏感词
         *
         * @param text 待检查的文本
         * @return 第一个匹配结果
         */
        public String match(String text) {
            return sensitiveWordTree.match(text);
        }

        /**
         * 标准匹配，匹配到最短关键词，并跳过已经匹配的关键词
         *
         * @param text 待检查的文本
         * @return 敏感词列表
         */
        public List<String> matchAll(String text) {
            return sensitiveWordTree.matchAll(text, -1, false, false);
        }
    }
}
