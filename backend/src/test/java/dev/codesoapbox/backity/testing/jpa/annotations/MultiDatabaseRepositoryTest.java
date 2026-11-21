package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Automatically generates child test classes for multiple database types, allowing repositories to be tested
/// against multiple databases in a single test run.
///
/// **Usage:**
/// ```
/// @MultiDatabaseRepositoryTest
/// @Transactional // Required by @JpaRepositoryTest
/// abstract class SomeJpaRepositoryIT {
///   //...
/// }
/// ```
///
/// Note that:
/// 1. You must run `mvn test-compile` for the IDE to see the child classes.
/// 2. You must run `mvn test-compile` every time you add a `@Nested` class to the abstract test class.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@MultiAnnotationTest(
        value = {
                PostgresRepositoryTest.class,
                H2RepositoryTest.class
        },
        stripFromClassNameRegex = "Abstract|RepositoryTest"
)
public @interface MultiDatabaseRepositoryTest {
}
