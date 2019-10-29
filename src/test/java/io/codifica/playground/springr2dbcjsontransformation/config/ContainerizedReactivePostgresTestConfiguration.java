package io.codifica.playground.springr2dbcjsontransformation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.codifica.playground.springr2dbcjsontransformation.config.support.DatasourceProps;
import io.codifica.playground.springr2dbcjsontransformation.config.support.MapToJsonConverter;
import io.codifica.playground.springr2dbcjsontransformation.config.support.JsonToMapConverter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@TestConfiguration
@EnableR2dbcRepositories("io.codifica.playground.springr2dbcjsontransformation.product")
public class ContainerizedReactivePostgresTestConfiguration extends AbstractR2dbcConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatasourceProps datasourceProps;

    private final PostgreSQLContainer<?> postgreSQLContainer;

    public ContainerizedReactivePostgresTestConfiguration() {
        this.postgreSQLContainer = new PostgreSQLContainer();
        this.postgreSQLContainer.start();
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(datasourceProps.getPrefix() + postgreSQLContainer.getUsername() + ":" +
                postgreSQLContainer.getPassword() + "@" + postgreSQLContainer.getContainerIpAddress() + ":" +
                postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) +
                "/" + postgreSQLContainer.getDatabaseName());
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new JsonToMapConverter(objectMapper));
        converters.add(new MapToJsonConverter(objectMapper));
        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }

    @Bean
    @FlywayDataSource
    public DataSource dataSource() {
        HikariConfig hk = new HikariConfig();
        hk.setJdbcUrl("jdbc:postgresql://" + postgreSQLContainer.getContainerIpAddress() +
                ":" + postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) + "/" +
                postgreSQLContainer.getDatabaseName());

        hk.setDriverClassName(org.postgresql.Driver.class.getCanonicalName());
        hk.setUsername(postgreSQLContainer.getUsername());
        hk.setPassword(postgreSQLContainer.getPassword());
        return new DockerizedDataSource(postgreSQLContainer, hk);
    }

    public class DockerizedDataSource extends HikariDataSource implements DisposableBean {

        private GenericContainer<?> container;

        public DockerizedDataSource(GenericContainer<?> container, HikariConfig config) {
            super(config);
            this.container = container;
        }

        @Override
        public void destroy() throws Exception {
            if (container != null && container.isRunning()) {
                container.stop();
            }
        }
    }

}
