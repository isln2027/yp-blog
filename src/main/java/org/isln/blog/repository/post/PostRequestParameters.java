package org.isln.blog.repository.post;

import java.util.Set;

public record PostRequestParameters(String search, Set<String> tags) {
}