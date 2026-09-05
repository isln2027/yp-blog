package org.isln.blog.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PostQueryResolver {

    public static final String TAG_PREFIX = "#";
    public static final String CHUNK_DELIMITER = " +";

    public static String getSearchQuery(String request) {
        if (request == null) {
            return "";
        }
        return Arrays.stream(request.split(CHUNK_DELIMITER))
                .filter(chunk -> !chunk.isBlank())
                .filter(chunk -> !chunk.startsWith(TAG_PREFIX))
                .collect(Collectors.joining(" "));
    }

    public static Set<String> getTags(String request) {
        if (request == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(request.split(CHUNK_DELIMITER))
                .filter(chunk -> !chunk.isBlank())
                .filter(chunk -> chunk.startsWith(TAG_PREFIX))
                .map(chunk -> chunk.substring(1))
                .collect(Collectors.toSet());
    }
}
