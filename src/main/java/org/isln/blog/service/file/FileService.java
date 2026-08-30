package org.isln.blog.service.file;

public interface FileService {
    void save(String fileName, byte[] file);
    byte[] get(String fileName);
    void delete(String fileName);
}
