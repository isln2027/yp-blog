package org.isln.blog.test.integration.configuration;

import org.isln.blog.service.file.FileService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestFileServiceConfiguration {
    @Bean
    @Primary
    public FileService getFileService() {
        return Mockito.mock(FileService.class);
    }
}
