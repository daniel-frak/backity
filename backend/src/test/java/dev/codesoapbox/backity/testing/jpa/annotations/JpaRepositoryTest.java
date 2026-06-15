package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositorySliceConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherSliceConfiguration;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControlExtension;
import dev.codesoapbox.backity.testing.mocking.MockBeansMatching;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import dev.codesoapbox.backity.testing.time.config.ResetClockTestExecutionListener;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation for a JPA repository test.
///
/// All tests are transactional and roll back at the end of each test.
///
/// If the test class is extending an abstract test class, `@Transactional` **must** be put on the abstract
/// class; otherwise transaction support may not work properly.
///
/// # Motivation for shared context
///
/// If we don't provide exactly the same configuration to every repository test, the Spring context will not get reused,
/// creating a small army of cached contexts, all with open DB connections.
/// At some point, these cached connections will overwhelm the database, making it return an exception:
///
/// "PSQLException: FATAL: sorry, too many clients already"
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
@TestExecutionListeners(listeners = ResetClockTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Import({
        // Common
        FakeTimeBeanConfig.class,

        // Testing tools
        DirectJpaPersistenceAdapter.class,
})
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = JpaRepositorySliceConfiguration.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = DirectJpaPersistenceStrategy.class
                )
        },
        useDefaultFilters = false
)
@MockBeansMatching(
        @ComponentScan(
                basePackageClasses = BackityApplication.class,
                includeFilters = @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = SpringApplicationEventPublisherSliceConfiguration.class
                ),
                useDefaultFilters = false
        )
)
@ExtendWith(EntityAuditControlExtension.class)
public @interface JpaRepositoryTest {
}
