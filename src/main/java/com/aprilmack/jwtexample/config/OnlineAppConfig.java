package com.aprilmack.jwtexample.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.time.Clock;

@Configuration
@Profile("online")
public class OnlineAppConfig {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.online")
    public DataSourceProperties onlineDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource() {
        return onlineDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}
