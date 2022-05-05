package dev.codesoapbox.backity.testing.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerWrapper extends PostgreSQLContainer<PostgresContainerWrapper> {

    public static final String DATASOURCE_URL = "spring.datasource.url";
    public static final String DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.password";

    private PostgresContainerWrapper(String dockerImageName) {
        super(dockerImageName);
    }

    public static PostgresContainerWrapper getContainer() {
        return new PostgresContainerWrapper("postgres:14.2");
    }

    public void setPostgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add(DATASOURCE_URL, this::getJdbcUrl);
        registry.add(DATASOURCE_USERNAME, this::getUsername);
        registry.add(DATASOURCE_PASSWORD, this::getPassword);
    }

    public void setPostgreSQLProperties(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                DATASOURCE_URL + "=" + getJdbcUrl(),
                DATASOURCE_USERNAME + "=" + getUsername(),
                DATASOURCE_PASSWORD + "=" + getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
