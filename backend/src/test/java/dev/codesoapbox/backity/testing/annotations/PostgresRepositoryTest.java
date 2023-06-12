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

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Testcontainers
@RepositoryTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres")
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
// Enable this once Liquibase is added:
//@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
public @interface PostgresRepositoryTest {
}
