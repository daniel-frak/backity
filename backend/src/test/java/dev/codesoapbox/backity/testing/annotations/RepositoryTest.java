package dev.codesoapbox.backity.testing.annotations;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@EnableJpaAuditing
@DirtiesContext
// Enable this once Liquibase is added:
//@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
public @interface RepositoryTest {
}
