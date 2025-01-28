package dev.codesoapbox.backity.testing.jpa.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * A wrapper for the {@link PostgreSQLContainer} to provide additional configuration
 * for Spring-based integration tests.
 * <p>
 * It simplifies the setup of a Postgres container by predefining the image version
 * and providing utilities for configuring the Spring application context with required Postgres properties.
 * </p>
 */
public class PostgresContainerWrapper extends PostgreSQLContainer<PostgresContainerWrapper> {

    public static final String DATASOURCE_URL = "spring.datasource.url";
    public static final String DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.password";

    private PostgresContainerWrapper(String dockerImageName) {
        super(dockerImageName);
    }

    public static PostgresContainerWrapper getContainer() {
        return new PostgresContainerWrapper("postgres:16.2");
    }

    public void setPostgreSQLProperties(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                DATASOURCE_URL + "=" + getJdbcUrl(),
                DATASOURCE_USERNAME + "=" + getUsername(),
                DATASOURCE_PASSWORD + "=" + getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
