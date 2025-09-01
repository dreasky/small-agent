package com.qik.agent.ai.advisor;


import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;

/**
 * 创建自定义顾问的工厂
 */
public class AdvisorFactory {

    /**
     * 创建检索增强的RAG知识库顾问
     *
     * @param vectorStore      向量存储
     * @param filterExpression 过滤表达式，使用FilterExpressionBuilder构建
     * @param augmenter        上下文查询增强器，可使用ContextualQueryAugmenterFactory工厂创建
     * @return RAG知识库顾问
     */
    public static Advisor createCustomRagAdvisor(
            VectorStore vectorStore,
            Filter.Expression filterExpression,
            ContextualQueryAugmenter augmenter
    ) {
        // 过滤表达式like:
        // Filter.Expression expression = new FilterExpressionBuilder()
        //      .eq("status", status)
        //      .build();

        // 创建文档检索器
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(filterExpression) // 过滤条件
                .similarityThreshold(0.5) // 相似度阈值
                .topK(3) // 返回文档数量
                .build();

        // 默认 order 为 0
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(augmenter)
                .build();
    }
}
