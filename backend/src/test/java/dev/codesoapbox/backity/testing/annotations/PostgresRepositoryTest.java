package dev.codesoapbox.backity.testing.annotations;

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
 * <p>
 * All tests are transactional and roll back at the end of each test.
 * <p>
 * If the test class is extending an abstract test class, {@code @Transactional} <b>must</b> be put on the abstract
 * class, otherwise transaction support may not work properly.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Testcontainers
@RepositoryTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres")
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
public @interface PostgresRepositoryTest {
}
