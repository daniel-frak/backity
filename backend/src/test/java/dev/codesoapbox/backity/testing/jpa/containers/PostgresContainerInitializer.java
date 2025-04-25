package dev.codesoapbox.backity.testing.jpa.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * {@link ApplicationContextInitializer} that configures a reusable Postgres container for integration testing.
 * <p>
 * The Spring environment is automatically configured to use the container.
 * <p>
 * Should typically be used as {@code @ContextConfiguration(initializers = PostgresContainerInitializer.class)}
 * on a slice test annotation.
 * <p>
 * Based on:
 * <a href="https://stackoverflow.com/a/68890310">https://stackoverflow.com/a/68890310</a>
 *
 * @see ApplicationContextInitializer
 * @see PreconfiguredPostgresContainer
 */
public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PreconfiguredPostgresContainer CONTAINER = new PreconfiguredPostgresContainer();

    private static final String DATASOURCE_URL = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD = "spring.datasource.password";

    public PostgresContainerInitializer() {
        CONTAINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                DATASOURCE_URL + "=" + CONTAINER.getJdbcUrl(),
                DATASOURCE_USERNAME + "=" + CONTAINER.getUsername(),
                DATASOURCE_PASSWORD + "=" + CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
