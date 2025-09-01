package com.qik.agent.model.bookmark;

import lombok.Data;

/**
 * 提供给ai的书签信息
 */
@Data
public class Bookmark {
    private String url;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookmark that = (Bookmark) o;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}