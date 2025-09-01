package com.qik.agent.utility.reptiles;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * @author : Qik 2025/8/11 19:05
 */
public class Reptiles<R> {

    private final TargetHandle targetHandle;

    private final ResultHandle<R> resultHandle;

    private final List<CleanRule> cleanRules;

    private final Set<String> htmlMetadataKey;

    private final Map<String, Object> additionalMetadata;

    private Reptiles(Builder<R> builder) {
        this.targetHandle = builder.targetHandle;
        this.resultHandle = builder.resultHandle;
        this.cleanRules = builder.cleanRules;
        this.htmlMetadataKey = builder.htmlMetadataKey;
        this.additionalMetadata = builder.additionalMetadata;
    }

    /**
     * 提取处理HTML中的元数据与内容
     *
     * @param url 目标url
     * @return 元数据与内容
     */
    public ReaderResult<R> parseHtml(String url) {
        // 获取html源码
        String pageSource = targetHandle.getHtml(url);

        // 解析源页面
        Document document = Jsoup.parse(pageSource);

        // 提取html中数据作为附加元数据
        additionalMetadata.put("title", document.title());
        for (String metaName : htmlMetadataKey) {
            // 如果附加元数据已有则跳过
            if (additionalMetadata.containsKey(metaName)) continue;

            Element selected = document.selectFirst("meta[name=" + metaName + "]");
            if (selected != null) {
                String content = selected.attr("content");
                additionalMetadata.put(metaName, content);
            }
        }

        // 清洗 Document
        for (Function<Document, Document> rule : cleanRules) {
            document = rule.apply(document);
        }

        // 获取结果内容
        return new ReaderResult<>(
                additionalMetadata,
                resultHandle.apply(document)
        );
    }



    public record ReaderResult<R>(Map<String, Object> metadata, R content) {
    }

    public static <R> Builder<R> builder(TargetHandle targetHandle, ResultHandle<R> resultHandle) {
        return new Builder<>(targetHandle, resultHandle);
    }

    public static final class Builder<R> {

        private final TargetHandle targetHandle;

        private final ResultHandle<R> resultHandle;

        private final List<CleanRule> cleanRules = new ArrayList<>();

        private final Set<String> htmlMetadataKey = new HashSet<>();

        private final Map<String, Object> additionalMetadata = new HashMap<>();


        private Builder(TargetHandle targetHandle, ResultHandle<R> resultHandle) {
            this.targetHandle = targetHandle;
            this.resultHandle = resultHandle;
        }

        /**
         * @param rule html 清理规则
         * @return builder self
         */
        public Builder<R> addCleanRule(CleanRule rule) {
            Assert.notNull(rule, "rule must not be null");
            cleanRules.add(rule);
            return this;
        }

        /**
         * @param rules html 清理规则
         * @return builder self
         */
        public Builder<R> addCleanRule(CleanRule... rules) {
            Assert.notNull(rules, "rule must not be null");
            cleanRules.addAll(List.of(rules));
            return this;
        }

        /**
         * 提取 html 中的 meta 信息作为 metadata
         *
         * @param key 元数据名称
         * @return builder self
         */
        public Builder<R> withHtmlMetadata(String key) {
            htmlMetadataKey.add(key);
            return this;
        }

        /**
         * 附加的 metadata
         *
         * @param key   元数据名称
         * @param value 元数据
         * @return builder self
         */
        public Builder<R> withAdditionalMetadata(String key, Object value) {
            Assert.notNull(key, "key must not be null");
            Assert.notNull(value, "value must not be null");
            this.additionalMetadata.put(key, value);
            return this;
        }

        /**
         * 附加的 metadata
         *
         * @param additionalMetadata 附加的 metadata Map
         * @return builder self
         */
        public Builder<R> withAdditionalMetadata(Map<String, Object> additionalMetadata) {
            Assert.notNull(additionalMetadata, "additionalMetadata must not be null");
            this.additionalMetadata.putAll(additionalMetadata);
            return this;
        }

        /**
         * @return the immutable configuration
         */
        public Reptiles<R> build() {
            return new Reptiles<>(this);
        }
    }
}
