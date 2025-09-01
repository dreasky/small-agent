package com.qik.agent.ai.tool;

import com.qik.agent.utility.reptiles.CleanRuleFactory;
import com.qik.agent.utility.reptiles.Reptiles;
import com.qik.agent.utility.reptiles.ResultHandleFactory;
import com.qik.agent.utility.reptiles.TargetHandleFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 网页抓取
 */
public class WebScrapingTool {

    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        Reptiles<String> reptiles = Reptiles.builder(
                        TargetHandleFactory.jsoupTargetHandle(),
                        ResultHandleFactory.markdownResultHandle()
                )
                .addCleanRule(
                        CleanRuleFactory.safelistClear(),
                        CleanRuleFactory.removeTagsClear(),
                        CleanRuleFactory.nestingTagClear(),
                        CleanRuleFactory.emptyTagsClear()
                )
                .build();

        return reptiles.parseHtml(url).content();
    }
}
