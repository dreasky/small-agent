package com.qik.agent.ai;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author : Qik 2025/8/27 10:26
 */
@Configuration
public class UserPromptTemplateFactory {

    @Bean
    public PromptTemplate optimizeCategoryTemplate(@Value("classpath:prompts/user-optimize-category.st") Resource resource) {
        return new PromptTemplate(resource);
    }

    @Bean
    public PromptTemplate groupBookmarksTemplate(@Value("classpath:prompts/user-group-bookmarks.st") Resource resource) {
        return new PromptTemplate(resource);
    }

}
