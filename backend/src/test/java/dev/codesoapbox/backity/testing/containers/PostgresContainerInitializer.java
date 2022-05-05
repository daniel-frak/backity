package dev.codesoapbox.backity.testing.containers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Based on:
 * https://stackoverflow.com/a/68890310
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
