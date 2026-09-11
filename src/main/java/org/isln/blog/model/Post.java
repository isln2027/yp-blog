package org.isln.blog.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Post {
    private Long id;
    private String title;
    private String text;
    private Set<String> tags;
    private Integer likesCount;
    private Integer commentsCount;

    public String getTextShort(int charCount) {
        if (text == null) {
            return null;
        }
        if (text.length() < charCount) {
            return text;
        } else {
            return text.substring(0, charCount) + "...";
        }
    }
}
