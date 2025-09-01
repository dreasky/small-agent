package com.qik.agent.ai.rag;

import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 自定义的向量储存工厂
 *
 * @author : Qik 2025/8/12 16:55
 */
@Configuration
public class VectorStoreFactory {

    /**
     * PgVector 向量储存
     *
     * @param jdbcTemplate         Jdbc数据库接口
     * @param properties           spring 配置
     * @param ollamaEmbeddingModel 文本嵌入模型(dashscopeEmbeddingModel | ollamaEmbeddingModel)
     * @return 向量储存
     */
    @Bean
    public VectorStore pgVectorStore(
            JdbcTemplate jdbcTemplate,
            PgVectorStoreProperties properties,
            EmbeddingModel ollamaEmbeddingModel
    ) {
        // 批处理策略
        TokenCountBatchingStrategy tokenCountBatchingStrategy = new TokenCountBatchingStrategy(
                EncodingType.CL100K_BASE,  // 指定编码类型
                8000,                      // 设置最大输入标记计数
                0.1                        // 设置保留百分比
        );

        return PgVectorStore.builder(jdbcTemplate, ollamaEmbeddingModel)
                .schemaName(properties.getSchemaName())     // 架构名 default: public
                .idType(properties.getIdType())             // Id类型 default: uuid
                .vectorTableName(properties.getTableName()) // 表名 default: vector_store
                .vectorTableValidationsEnabled(properties.isSchemaValidation()) // 是否架构验证 default: false
                .dimensions(properties.getDimensions())     // 维度 config:
                .distanceType(properties.getDistanceType()) // 距离计算类型 default: COSINE_DISTANCE 余弦
                .removeExistingVectorStoreTable(properties.isRemoveExistingVectorStoreTable())  // 是否删除现有表 default: false
                .indexType(properties.getIndexType())   // 序列类型 default: HNSW
                .initializeSchema(properties.isInitializeSchema())  // 初始化架构 config: true
//                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)) // 观测注册表
//                .customObservationConvention(customObservationConvention.getIfAvailable(() -> null)) // 自定义观察约定
                .batchingStrategy(tokenCountBatchingStrategy)   // 批处理策略
                .maxDocumentBatchSize(properties.getMaxDocumentBatchSize()) // 最大文档批量尺寸 default: 10000
                .build();
    }
}
