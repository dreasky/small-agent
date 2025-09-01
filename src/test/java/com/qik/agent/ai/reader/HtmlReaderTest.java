package com.qik.agent.ai.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * @author : Qik 2025/9/1 15:40
 */
@SpringBootTest
class HtmlReaderTest {

    @Test
    public void get() {
//        String url = "https://java2ai.com/docs/1.0.0.2/spring-ai-sourcecode-explained/chapter-6-rag-enhanced-qa/";
        String url = "https://docs.langchain4j.info/get-started";

        HtmlReader htmlReader = new HtmlReader(url);
        List<Document> documents = htmlReader.get();
        Assertions.assertNotNull(documents);
    }
}