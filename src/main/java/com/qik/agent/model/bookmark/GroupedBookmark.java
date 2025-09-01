package com.qik.agent.model.bookmark;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ai分类后的书签实体类
 */
@Data
public class GroupedBookmark implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 原始书签url
     */
    private String url;

    /**
     * 原始书签name
     */
    private String name;

    /**
     * 分类信息
     */
    private String[] bookmarkPaths;

    /**
     * 分类置信度
     */
    private Double confidenceScore;
}