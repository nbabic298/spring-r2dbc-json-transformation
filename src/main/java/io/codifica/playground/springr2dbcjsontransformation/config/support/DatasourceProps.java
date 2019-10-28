package io.codifica.playground.springr2dbcjsontransformation.config.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("postgres.datasource")
public class DatasourceProps {

    private String prefix;

    private String host;

    private String port;

    private String database;

    private String username;

    private String password;

}
