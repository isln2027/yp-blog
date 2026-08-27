package org.isln.blog.configuration;

import tools.jackson.databind.json.JsonMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public JsonMapper getMapper() {
        return new JsonMapper();
    }
}
