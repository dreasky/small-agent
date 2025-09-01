package com.qik.agent.model.bookmark.dto;

import com.qik.agent.model.bookmark.Bookmark;
import com.qik.agent.model.bookmark.CategoryTemplate;
import lombok.Data;

import java.util.List;

/**
 * 分组书签请求DTO
 */
@Data
public class GroupBookmarksRequest {
    private List<Bookmark> bookmarks;
    private CategoryTemplate categoryTemplate;
}
