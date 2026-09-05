package org.isln.blog.test.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.isln.blog.service.file.FileService;
import org.isln.blog.service.file.FileSystemFileService;
import org.isln.blog.test.unit.configuration.TestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {TestConfiguration.class})
public class FileManagementTest {
    private static final byte[] PNG_CONTENT = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00};
    @Autowired
    private FileService fileService;

    @Test
    public void createFileTest() {
        String fileName = "file.txt";

        fileService.save(fileName, PNG_CONTENT);

        byte[] file = fileService.get(fileName);
        assertThat(file).containsExactly(PNG_CONTENT);
    }

    @Test
    public void overwriteFileTest() {
        String fileName = "file.txt";
        fileService.save(fileName, PNG_CONTENT);
        byte[] newContent = new byte[PNG_CONTENT.length + 1];
        newContent[PNG_CONTENT.length] = (byte) 0x01;
        fileService.save(fileName, newContent);

        byte[] file = fileService.get(fileName);
        assertThat(file).containsExactly(newContent);
    }

    @Test
    public void deleteFileTest() {
        String fileName = "file.txt";
        fileService.save(fileName, PNG_CONTENT);

        fileService.delete(fileName);

        byte[] file = fileService.get(fileName);
        assertThat(file).isEmpty();
    }

    @AfterEach
    public void cleanup() {
        Path path = FileSystemFileService.getUploadDirectory();
        try {
            if (Files.exists(path)) {
                try (var files = Files.walk(path)) {
                    files.sorted(Comparator.reverseOrder())
                            .forEach(p -> {
                                        try {
                                            Files.deleteIfExists(p);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
