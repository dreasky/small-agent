package com.qik.agent.model.bookmark;

import lombok.Data;

/**
 * 分类模板根节点（每个用户类型对应一个根节点，如“开发者模板”“普通用户模板”）
 */
@Data
public class CategoryTemplate {
    /**
     * 模板唯一标识（如“developer-template”“normal-user-template”）
     */
    private String templateId;

    /**
     * 模板名称（供用户选择，如“按技术栈分类（开发者）”“按使用场景分类（普通用户）”）
     */
    private String templateName;

    /**
     * 模板描述（简要说明适用场景，辅助用户选择）
     */
    private String description;

    /**
     * 根分类节点（整个分类体系的入口）
     */
    private CategoryNode rootNode;
}