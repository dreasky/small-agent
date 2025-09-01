package com.qik.agent.ai.rag;

import com.qik.agent.ai.reader.HtmlReader;
import com.qik.agent.ai.reader.MarkdownMetadataEnricher;
import com.qik.agent.utility.LoadUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

/**
 * @author : Qik 2025/9/1 15:53
 */
@SpringBootTest
class VectorStoreFactoryTest {

    @Resource
    VectorStore pgVectorStore;

    @Resource
    MarkdownMetadataEnricher enricher;

    @Test
    void testMarkdownLoader() {
        // 添加文档
//        loadMarkdown();

        // 查询条件
        SearchRequest search = SearchRequest.builder()
                .query("单身")
                .similarityThreshold(0.5)
                .topK(5)
                .build();
        // 相似度查询
        List<Document> results = pgVectorStore.similaritySearch(search);
        Assertions.assertNotNull(results);
    }

    @Test
    void testUrlsLoader() {
        // 添加文档
//        loadUrls();

        // 查询条件
        SearchRequest search = SearchRequest.builder()
                .query("在Gradle中配置langchain4j")
                .similarityThreshold(0.5)
                .topK(5)
                .build();
        // 相似度查询
        List<Document> results = pgVectorStore.similaritySearch(search);
        Assertions.assertNotNull(results);
    }

    private void loadUrls() {
        // 读取资源
        org.springframework.core.io.Resource[] resources =
                LoadUtil.loadResource("classpath:/document/links/*.txt");

        for (org.springframework.core.io.Resource resource : resources) {
            Set<String> urls = LoadUtil.loadUrls(resource);
            for (String url : urls) {
                HtmlReader reader = new HtmlReader(url);
                List<Document> documents = reader.get();

                // Document处理优化
                documents = enricher.enrichment(documents);

                // 添加入向量储存
                pgVectorStore.add(documents);
            }
        }
    }

    private void loadMarkdown() {
        // 读取资源
        org.springframework.core.io.Resource[] resources =
                LoadUtil.loadResource("classpath:/document/markdown/*.md");

        for (org.springframework.core.io.Resource resource : resources) {
            String filename = resource.getFilename();
            // 提取文档倒数第 3 和第 2 个字作为状态标签（文档名格式固定，读取的[单身，已婚，恋爱]）
            // todo 更灵活的提取标签内容
            assert filename != null;
            String status = filename.substring(filename.length() - 6, filename.length() - 4);

            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata("filename", filename)
                    .withAdditionalMetadata("status", status)
                    .build();
            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
            List<Document> documents = reader.get();

            // Document处理优化
            documents = enricher.enrichment(documents);

            // 加入向量储存
            pgVectorStore.add(documents);
        }
    }
}