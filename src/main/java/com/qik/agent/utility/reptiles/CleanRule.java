package com.qik.agent.utility.reptiles;

import org.jsoup.nodes.Document;

import java.util.function.Function;

/**
 * @author : Qik 2025/8/14 21:32
 */
public interface CleanRule extends Function<Document, Document> {
    @Override
    Document apply(Document document);
}
