package com.qik.agent.ai.reader;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Qik 2025/9/1 15:58
 */
@Component
public class MarkdownMetadataEnricher {

    private final ChatModel chatModel;

    public MarkdownMetadataEnricher(ChatModel dashscopeChatModel) {
        this.chatModel = dashscopeChatModel;
    }

    public List<Document> enrichment(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher =
                new KeywordMetadataEnricher(chatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
