package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFilesCopiesReadModelJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import dev.codesoapbox.backity.testing.time.config.ResetClockTestExecutionListener;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

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
 * class; otherwise transaction support may not work properly.
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
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
@TestExecutionListeners(listeners = ResetClockTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Import({
        // Common
        FakeTimeBeanConfig.class
})
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = JpaRepositoryBeanConfiguration.class
        ),
        useDefaultFilters = false
)
@MockitoBean(types = {
        DomainEventPublisher.class
})
@MockitoSpyBean(types = {
        GameWithFilesCopiesReadModelJpaEntityMapper.class,
        AuditingHandler.class
})
public @interface JpaRepositoryTest {
}
