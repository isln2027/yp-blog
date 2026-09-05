package org.isln.blog.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.isln.blog.exceptions.FileOperationException;
import org.springframework.stereotype.Service;

@Service
public class FileSystemFileService implements FileService {
    @Override
    public void save(String fileName, byte[] file) {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename and file must not be null");
        }
        try {
            Path uploadDirectory = getUploadDirectory();
            Path filePath = uploadDirectory.resolve(fileName);
            Files.write(filePath, file);
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public byte[] get(String fileName) {
        try {
            Path uploadDirectory = getUploadDirectory();
            Path filePath = uploadDirectory.resolve(fileName);
            if (Files.exists(filePath)) {
                return Files.readAllBytes(filePath);
            } else {
                return new byte[]{};
            }
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Path uploadDirectory = getUploadDirectory();
            Path filePath = uploadDirectory.resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    public static Path getUploadDirectory() {
        try {
            Path uploadDirectory = Paths.get("uploads", "posts");
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }
            return uploadDirectory;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
