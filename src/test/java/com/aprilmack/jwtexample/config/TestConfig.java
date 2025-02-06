package com.aprilmack.jwtexample.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
@Profile("test")
public class TestConfig {
    public static final Instant CURRENT_TIME = Instant.parse("2025-02-01T08:30:00Z");

    @Bean
    public Clock clock() {
        return Clock.fixed(CURRENT_TIME, ZoneOffset.UTC);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.test")
    public DataSourceProperties testDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource() {
        return testDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}
