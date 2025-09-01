package com.qik.agent.model.bookmark;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : Qik 2025/8/22 16:55
 */
@Data
public class Categories implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分组名
     */
    private String name;

    /**
     * 层级路径
     */
    private List<String> path;
}
