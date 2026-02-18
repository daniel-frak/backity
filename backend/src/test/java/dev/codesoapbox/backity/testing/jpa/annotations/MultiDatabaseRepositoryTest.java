package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.buildtools.MultiAnnotationTest;
import jakarta.transaction.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@MultiAnnotationTest(
        value = {
                PostgresRepositoryTest.class,
                H2RepositoryTest.class
        },
        stripFromClassNameRegex = "Abstract|RepositoryTest"
)
@Transactional
public @interface MultiDatabaseRepositoryTest {
}
