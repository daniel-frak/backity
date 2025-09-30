package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.jpa.SharedJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import dev.codesoapbox.backity.testing.time.config.ResetClockTestExecutionListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
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
public @interface ConfigureJpaRepositoryTestBeans {
}
