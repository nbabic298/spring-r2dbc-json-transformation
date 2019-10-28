package io.codifica.playground.springr2dbcjsontransformation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codifica.playground.springr2dbcjsontransformation.config.support.DatasourceProps;
import io.codifica.playground.springr2dbcjsontransformation.config.support.MapToStringConverter;
import io.codifica.playground.springr2dbcjsontransformation.config.support.StringToMapConverter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;
import java.util.List;


@Profile("!test")
@Configuration
@AllArgsConstructor
@EnableR2dbcRepositories("io.codifica.playground.springr2dbcjsontransformation.product")
public class ReactivePostgresConfig extends AbstractR2dbcConfiguration {

    private final ObjectMapper objectMapper;

    private final DatasourceProps datasourceProps;

    /** @noinspection NullableProblems*/
    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(datasourceProps.getPrefix() + datasourceProps.getUsername() + ":" +
                datasourceProps.getPassword() + "@" + datasourceProps.getHost() + ":" + datasourceProps.getPort() +
                "/" + datasourceProps.getDatabase());
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new StringToMapConverter(objectMapper));
        converters.add(new MapToStringConverter(objectMapper));
        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }

}
