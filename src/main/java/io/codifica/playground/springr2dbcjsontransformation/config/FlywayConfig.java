package io.codifica.playground.springr2dbcjsontransformation.config;

import io.codifica.playground.springr2dbcjsontransformation.config.support.DatasourceProps;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile("!test")
@Configuration
@AllArgsConstructor
public class FlywayConfig {

    private final DatasourceProps datasourceProps;

    @Bean
    @FlywayDataSource
    public DataSource dataSource() {
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName("org.postgresql.Driver");
        driver.setUrl("jdbc:postgresql://" + datasourceProps.getHost() + ":" + datasourceProps.getPort() + "/"
                + datasourceProps.getDatabase());
        driver.setUsername(datasourceProps.getUsername());
        driver.setPassword(datasourceProps.getPassword());
        return driver;
    }

}
