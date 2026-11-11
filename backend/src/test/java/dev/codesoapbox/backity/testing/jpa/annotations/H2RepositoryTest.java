package dev.codesoapbox.backity.testing.jpa.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation for a JPA test using an in-memory H2 database.
///
/// All tests are transactional and roll back at the end of each test.
///
/// If the test class is extending an abstract test class, `@Transactional` **must** be put on the abstract
/// class, otherwise transaction support may not work properly.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JpaRepositoryTest
public @interface H2RepositoryTest {
}
