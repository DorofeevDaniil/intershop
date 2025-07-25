package ru.custom.intershop.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LiquibaseConfig {
    @Value("${spring.liquibase.parameters.schemaName}")
    private String schemaName;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.xml");
        liquibase.setDataSource(dataSource);

        Map<String, String> params = new HashMap<>();
        params.put("schemaName", schemaName);
        liquibase.setChangeLogParameters(params);

        return liquibase;
    }
}