package dev.codesoapbox.backity.testing.jpa.annotations;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a JPA repository test.
 * <p>
 * All tests are transactional and roll back at the end of each test.
 * <p>
 * If the test class is extending an abstract test class, {@code @Transactional} <b>must</b> be put on the abstract
 * class, otherwise transaction support may not work properly.
 * <p>
 * <h1>Motivation for shared context</h1>
 * <p>
 * If we don't provide exactly the same configuration to every repository test, the Spring Boot context will not get
 * reused, creating a small army of cached contexts, all with open DB connections.
 * At some point, these cached connections will overwhelm the database, making it return an exception:
 * <p>
 * "PSQLException: FATAL: sorry, too many clients already"
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
@ConfigureJpaRepositoryTestBeans
public @interface JpaRepositoryTest {
}
