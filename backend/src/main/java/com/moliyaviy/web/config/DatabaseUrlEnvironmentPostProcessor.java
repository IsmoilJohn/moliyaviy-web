package com.moliyaviy.web.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Railway's Postgres plugin exposes both a single DATABASE_URL
 * ("postgres://user:pass@host:port/db") and the individual PGHOST/PGPORT/
 * PGUSER/PGPASSWORD/PGDATABASE variables. application-prod.yml is written
 * against the individual variables; this only steps in when a deployment
 * has wired up DATABASE_URL alone, converting it into the same
 * spring.datasource.* properties so both wiring styles work.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getProperty("PGHOST") != null) {
            return;
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        try {
            URI uri = new URI(databaseUrl);
            String[] userInfo = uri.getUserInfo() != null ? uri.getUserInfo().split(":", 2) : new String[0];
            String username = userInfo.length > 0 ? userInfo[0] : "";
            String password = userInfo.length > 1 ? userInfo[1] : "";
            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath()
                    + "?stringtype=unspecified";

            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);
            properties.put("spring.datasource.username", username);
            properties.put("spring.datasource.password", password);

            environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlOverride", properties));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid DATABASE_URL: " + databaseUrl, e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
