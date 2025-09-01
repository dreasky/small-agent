package com.qik.agent.service;

import com.qik.agent.model.bookmark.Bookmark;
import com.qik.agent.model.bookmark.CategoryTemplate;
import com.qik.agent.model.bookmark.GroupedBookmark;

import java.util.List;

/**
 * @author : Qik 2025/8/27 12:23
 */
public interface BookmarkAgentService {
    /**
     * ai优化书签分类模板
     *
     * @param categoryTemplate 旧模板
     * @return 新模板
     */
    CategoryTemplate optimizeCategoryTemplate(CategoryTemplate categoryTemplate);

    /**
     * 使用AI对书签进行分类
     *
     * @param bookmarks        包含内容的书签列表
     * @param categoryTemplate 分类模板
     * @return 分类后的书签列表
     */
    List<GroupedBookmark> groupBookmarks(List<Bookmark> bookmarks, CategoryTemplate categoryTemplate);

}
