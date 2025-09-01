package com.qik.agent.model.bookmark;

import lombok.Data;

import java.util.List;

/**
 * 分类节点（树形结构的核心，表达单个分类及其子分类）
 */
@Data
public class CategoryNode {
    /**
     * 节点唯一标识（如“tech”“frontend”，用于内部关联）
     */
    private String nodeId;

    /**
     * 分类名称（AI实际参考的分类名，如“技术”“前端”，需简洁明确）
     */
    private String name;

    /**
     * 分类描述（关键！帮助AI理解该分类的范围，避免歧义）
     */
    private String description;

    /**
     * 示例书签（关键！给AI具体案例，明确该分类包含什么内容）
     */
    private List<String> examples;

    /**
     * 子分类节点（若为叶子节点，children为空）
     */
    private List<CategoryNode> children;

    /**
     * 层级限制（1-3级，避免AI过度嵌套）
     */
    private int level;

    /**
     * 是否允许AI在该节点下新增子分类（false=严格遵循模板，true=允许有限扩展）
     */
    private boolean allowExtension;
}