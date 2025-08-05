package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.testing.jpa.containers.PostgresContainerInitializer;
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
 * Mocks all repository dependencies.
 * <p>
 * An instance of PostgreSQL will start before the first test and will be shared between all tests.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * Creating many unique Spring Boot contexts can lead to the context cache filling up and evicting other,
 * equally expensive contexts.
 * <p>
 * The database connection pool is also likely to run out of connections,
 * randomly failing tests with errors such as "PSQLException: FATAL: sorry, too many clients already".
 * <p>
 * Thus, making all repository tests share a single application context should protect against cache eviction slowing
 * down the tests, as well as test flakiness due to connection issues.
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
