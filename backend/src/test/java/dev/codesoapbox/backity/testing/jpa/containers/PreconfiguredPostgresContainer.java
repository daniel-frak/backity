package dev.codesoapbox.backity.testing.jpa.containers;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * A wrapper for the {@link PostgreSQLContainer} which simplifies its setup by predefining the image version.
 */
public class PreconfiguredPostgresContainer extends PostgreSQLContainer<PreconfiguredPostgresContainer> {

    private static final String IMAGE_VERSION = "postgres:17.4";

    public PreconfiguredPostgresContainer() {
        super(IMAGE_VERSION);
    }
}
