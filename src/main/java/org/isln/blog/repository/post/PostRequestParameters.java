package org.isln.blog.repository.post;

import java.util.Set;

public record PostRequestParameters(String query, Set<String> tags) {
    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public PostRequestParameters() {
        this(null, null);
    }
}