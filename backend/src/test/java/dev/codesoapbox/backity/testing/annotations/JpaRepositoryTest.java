package dev.codesoapbox.backity.testing.annotations;

import dev.codesoapbox.backity.core.shared.config.SharedRepositoryTestConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a JPA test.
 * <p>
 * All tests are transactional and roll back at the end of each test.
 * <p>
 * If the test class is extending an abstract test class, {@code @Transactional} <b>must</b> be put on the abstract
 * class, otherwise transaction support may not work properly.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
@Import(SharedRepositoryTestConfig.class)
public @interface JpaRepositoryTest {
}
