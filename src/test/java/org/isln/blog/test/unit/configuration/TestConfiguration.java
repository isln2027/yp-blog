package org.isln.blog.test.unit.configuration;

import org.isln.blog.repository.post.PostRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("org.isln.blog.service")
public class TestConfiguration {

    @Bean
    @Primary
    public PostRepository mockOrderRepository() {
        return Mockito.mock(PostRepository.class);
    }
}