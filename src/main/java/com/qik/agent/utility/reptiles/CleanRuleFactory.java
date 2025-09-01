package com.qik.agent.utility.reptiles;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 清理工厂
 *
 * @author : Qik 2025/9/1 16:10
 */
public class CleanRuleFactory {

    public static CleanRule safelistClear() {
        Safelist whitelist = Safelist
                // 使用更宽松的安全列表（允许更多标签和属性）
                .relaxed()
                // 额外允许style属性
                .addAttributes("pre", "class")
                // 移除href属性中可能的javascript协议
                .removeProtocols("a", "href", "javascript")
                .preserveRelativeLinks(true);
        Cleaner cleaner = new Cleaner(whitelist);

        return cleaner::clean;
    }

    public static CleanRule emptyTagsClear() {
        return document -> {
            document.getAllElements().removeIf(el -> el.text().trim().isEmpty());
            return document;
        };
    }

    public static CleanRule nestingTagClear() {
        return new CleanRule() {
            @Override
            public Document apply(Document document) {
                // 从根元素开始递归处理
                Element root = document.body();
                expandElementRecursively(root);
                return document;
            }

            /**
             * 递归展开元素
             *
             * @param element 要处理的元素
             * @return 是否发生了展开操作
             */
            private boolean expandElementRecursively(Element element) {
                if (element == null) return false;

                boolean expanded = false;
                List<Element> children = new ArrayList<>(element.children());

                // 先递归处理所有子元素
                for (Element child : children) {
                    if (expandElementRecursively(child)) {
                        expanded = true;
                    }
                }

                // 处理当前元素
                if (children.size() == 1) {
                    Element child = children.getFirst();
                    Element parent = element.parent();

                    // 父子标签名重复
                    boolean tagNameRepeat = child.tagName().equals(element.tagName());
                    if (parent != null && tagNameRepeat) {
                        // 记录当前节点在父节点中的位置
                        int index = element.siblingIndex();

                        // 移动子节点并删除当前节点
                        parent.insertChildren(index, element.childNodes());
                        element.remove();
                        expanded = true;
                    }
                }
                return expanded;
            }
        };
    }

    public static CleanRule removeTagsClear() {
        Set<String> removeTags = Set.of(
                "script", "style", "iframe", "noscript", "svg", "canvas",
                "footer", "nav", "aside", "link", "meta", "form"
        );

        return document -> {
            for (String removeTag : removeTags) {
                document.select(removeTag).remove();
            }
            return document;
        };
    }
}
