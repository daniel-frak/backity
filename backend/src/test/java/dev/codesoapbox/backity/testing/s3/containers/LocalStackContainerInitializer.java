package dev.codesoapbox.backity.testing.s3.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;

/**
 * {@link ApplicationContextInitializer} that configures a reusable LocalStack container for integration testing.
 * <p>
 * The Spring environment is automatically configured to use the container.
 * <p>
 * Should typically be used as {@code @ContextConfiguration(initializers = LocalStackContainerInitializer.class)}
 * on a slice test annotation.
 * <p>
 * Based on:
 * <a href="https://stackoverflow.com/a/68890310">https://stackoverflow.com/a/68890310</a>
 *
 * @see ApplicationContextInitializer
 * @see PreconfiguredLocalStackContainer
 */
public class LocalStackContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PreconfiguredLocalStackContainer CONTAINER = new PreconfiguredLocalStackContainer();
    private static final String AWS_REGION_PROPERTY = "spring.cloud.aws.region.static";
    private static final String S3_ENDPOINT_PROPERTY = "spring.cloud.aws.s3.endpoint";
    private static final String ACCESS_KEY_PROPERTY = "spring.cloud.aws.credentials.access-key";
    private static final String SECRET_KEY_PROPERTY = "spring.cloud.aws.credentials.secret-key";

    public LocalStackContainerInitializer() {
        CONTAINER.withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3)
                .start();
    }

    public static PreconfiguredLocalStackContainer getContainer() {
        return CONTAINER;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                AWS_REGION_PROPERTY + "=" + CONTAINER.getRegion(),
                S3_ENDPOINT_PROPERTY + "=" + CONTAINER.getEndpointOverride(LocalStackContainer.Service.S3),
                ACCESS_KEY_PROPERTY + "=" + CONTAINER.getAccessKey(),
                SECRET_KEY_PROPERTY + "=" + CONTAINER.getSecretKey()
        ).applyTo(applicationContext.getEnvironment());
    }
}
