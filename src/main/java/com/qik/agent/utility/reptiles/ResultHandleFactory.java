package com.qik.agent.utility.reptiles;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * @author : Qik 2025/8/30 19:54
 */
public class ResultHandleFactory {

    public static ResultHandle<String> markdownResultHandle() {
        MutableDataSet option = new MutableDataSet();
        // todo option自定义配置
        return document -> FlexmarkHtmlConverter.builder(option).build().convert(document);
    }

}
