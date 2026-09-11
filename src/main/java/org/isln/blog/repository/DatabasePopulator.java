package org.isln.blog.repository;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

@Service
public class DatabasePopulator {
    private final Resource schema;
    private final DataSource dataSource;

    public DatabasePopulator(DataSource dataSource, @Value("classpath:schema.sql") Resource schema) {
        this.schema = schema;
        this.dataSource = dataSource;
    }

    @EventListener
    public void populate(ContextRefreshedEvent event) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schema);
        populator.execute(dataSource);
    }
}
