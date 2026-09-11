package org.isln.blog.exceptions;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Exception cause) {
        super(message, cause);
    }
}
