package dev.codesoapbox.backity.testing.http.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.ControllerBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import dev.codesoapbox.backity.testing.mocking.MockBeansMatching;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import dev.codesoapbox.backity.testing.time.config.ResetClockTestExecutionListener;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.*;

/**
 * Annotation for a controller test.
 * <p>
 * Mocks every use case and injects a fake clock bean.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for controller tests does not take a long time, it can lead to the context
 * cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all controller tests share a single application context should protect against cache eviction slowing
 * down the tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
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
                classes = ControllerBeanConfiguration.class
        ),
        useDefaultFilters = false
)
@MockBeansMatching(
        @ComponentScan(
                basePackageClasses = BackityApplication.class,
                includeFilters = @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = UseCaseBeanConfiguration.class
                ),
                useDefaultFilters = false
        )
)
public @interface ControllerTest {
}
