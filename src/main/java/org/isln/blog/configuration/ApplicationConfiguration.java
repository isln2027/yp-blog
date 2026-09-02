package org.isln.blog.configuration;


import tools.jackson.databind.json.JsonMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.isln.blog.service")
public class ApplicationConfiguration {
    @Bean
    public JsonMapper getMapper() {
        return JsonMapper.builder().build();
    }
}
