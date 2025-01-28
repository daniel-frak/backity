package dev.codesoapbox.backity.testing.s3.containers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;

/**
 * {@link ApplicationContextInitializer} that configures a reusable LocalStack container for integration testing.
 * <p>
 * The Spring environment is automatically configured to use the LocalStack container.
 * <p>
 * Should typically be used as {@code @ContextConfiguration(initializers = LocalStackContainerInitializer. class)}
 * on a slice test annotation.
 * <p>
 * Based on:
 * <a href="https://stackoverflow.com/a/68890310">https://stackoverflow.com/a/68890310</a>
 *
 * @see ApplicationContextInitializer
 * @see LocalStackContainerWrapper
 */
public class LocalStackContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final LocalStackContainerWrapper LOCALSTACK_CONTAINER = LocalStackContainerWrapper.get();

    public LocalStackContainerInitializer() {
        LOCALSTACK_CONTAINER.withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3)
                .start();
    }

    public static LocalStackContainerWrapper getContainer() {
        return LOCALSTACK_CONTAINER;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LOCALSTACK_CONTAINER.setProperties(applicationContext);
    }
}
