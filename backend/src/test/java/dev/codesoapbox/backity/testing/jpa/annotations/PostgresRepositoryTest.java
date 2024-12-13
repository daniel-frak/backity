package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.testing.containers.PostgresContainerInitializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a JPA test using a PostgreSQL TestContainers instance.
 * <p>
 * An instance of PostgreSQL will start before the first test and will be shared between all tests.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Testcontainers
@JpaRepositoryTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres")
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
public @interface PostgresRepositoryTest {
}
