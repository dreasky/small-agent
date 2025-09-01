package com.qik.agent.utility;

import cn.hutool.core.io.file.FileReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : Qik 2025/8/13 22:13
 */
public class LoadUtil {

    public static Set<String> loadUrls(Resource resource) {
        try {
            FileReader fileReader = new FileReader(resource.getFile());
            return fileReader.read(reader -> {
                Set<String> collection = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;
                    collection.add(line);
                }
                return collection;
            });
        } catch (IOException e) {
            throw new RuntimeException("load urls error, filename:" + resource.getFilename(), e);
        }
    }

    public static Resource[] loadResource(String locationPattern) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            return resolver.getResources(locationPattern);
        } catch (IOException e) {
            throw new RuntimeException("load resource error, locationPattern: " + locationPattern, e);
        }
    }
}
