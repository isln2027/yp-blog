package org.isln.blog.repository.post;

import java.util.Set;

public record PostRequestParameters(int pageNumber, int pageSize, String search, Set<String> tags) {
}