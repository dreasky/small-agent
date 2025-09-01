package com.qik.agent.ai.reader;

import com.qik.agent.utility.reptiles.CleanRuleFactory;
import com.qik.agent.utility.reptiles.Reptiles;
import com.qik.agent.utility.reptiles.ResultHandleFactory;
import com.qik.agent.utility.reptiles.TargetHandleFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 将url链接中的html内容转换为markdown后进行读取
 *
 * @author : Qik 2025/8/7 22:20
 */
public class HtmlReader implements DocumentReader {

    private final String url;

    private final Reptiles<String> organizer;

    public HtmlReader(String url) {
        this.url = url;
        this.organizer = Reptiles.builder(
                        TargetHandleFactory.jsoupTargetHandle(),
                        ResultHandleFactory.markdownResultHandle()
                )
                .addCleanRule(CleanRuleFactory.safelistClear())
                .withHtmlMetadata("keywords")
                .withHtmlMetadata("description")
                .build();
    }

    @Override
    public List<Document> get() {
        // 解析清理html 并获取数据
        Reptiles.ReaderResult<String> readerResult = organizer.parseHtml(url);

        // 将String内容包装为Resource
        org.springframework.core.io.Resource markdownResource = new ByteArrayResource(readerResult.content().getBytes(StandardCharsets.UTF_8));
        // markdown 读取
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withAdditionalMetadata(readerResult.metadata())
                .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(markdownResource, config);
        return reader.get();
    }
}
