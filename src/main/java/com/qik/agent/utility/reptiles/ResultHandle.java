package com.qik.agent.utility.reptiles;

import org.jsoup.nodes.Document;

import java.util.function.Function;

/**
 * @author : Qik 2025/8/30 19:52
 */
public interface ResultHandle<T> extends Function<Document, T> {

    @Override
    T apply(Document document);

}
