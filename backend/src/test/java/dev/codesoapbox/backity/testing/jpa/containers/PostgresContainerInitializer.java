package dev.codesoapbox.backity.testing.jpa.containers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * {@link ApplicationContextInitializer} that configures a reusable Postgres container for integration testing.
 * <p>
 * The Spring environment is automatically configured to use the Postgres container.
 * <p>
 * Should typically be used as {@code @ContextConfiguration(initializers = PostgresContainerInitializer. class)}
 * on a slice test annotation.
 * <p>
 * Based on:
 * <a href="https://stackoverflow.com/a/68890310">https://stackoverflow.com/a/68890310</a>
 *
 * @see ApplicationContextInitializer
 * @see PostgresContainerWrapper
 */
public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgresContainerWrapper postgres = PostgresContainerWrapper.getContainer();

    public PostgresContainerInitializer() {
        postgres.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        postgres.setPostgreSQLProperties(applicationContext);
    }
}
