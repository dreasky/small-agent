package com.qik.agent.controller;

import com.qik.agent.common.BaseResponse;
import com.qik.agent.common.ResultUtils;
import com.qik.agent.model.bookmark.CategoryTemplate;
import com.qik.agent.model.bookmark.GroupedBookmark;
import com.qik.agent.model.bookmark.dto.GroupBookmarksRequest;
import com.qik.agent.service.BookmarkAgentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    @Resource
    private BookmarkAgentService bookmarkAgent;

    /**
     * Ai 优化分类模板
     */
    @PostMapping("/optimize-category")
    public BaseResponse<CategoryTemplate> optimizeCategoryTemplate(
            @RequestBody CategoryTemplate categoryTemplate
    ) {
        CategoryTemplate optimizedCategoryTemplate = bookmarkAgent.optimizeCategoryTemplate(categoryTemplate);
        return ResultUtils.success(optimizedCategoryTemplate);
    }

    /**
     * Ai 分组书签
     */
    @PostMapping("/group-bookmarks")
    public BaseResponse<List<GroupedBookmark>> groupBookmarks(
            @RequestBody GroupBookmarksRequest request
    ) {
        List<GroupedBookmark> groupedBookmarks = bookmarkAgent.groupBookmarks(request.getBookmarks(), request.getCategoryTemplate());
        return ResultUtils.success(groupedBookmarks);
    }
}