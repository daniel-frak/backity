package dev.codesoapbox.backity.testing.scheduling.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.config.slices.GameProviderServiceConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringSchedulerBeanConfiguration;
import dev.codesoapbox.backity.testing.mocking.MockBeansMatching;
import dev.codesoapbox.backity.testing.scheduling.RegisteredSchedulers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.support.NoOpTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/// Annotation for a Spring scheduler test.
///
/// # Motivation for shared context
///
/// Creating many unique Spring contexts can lead to the context cache filling up and evicting other
/// (potentially expensive) contexts.
///
/// Thus, making all tests of the same slice share a single application context should
/// protect against cache eviction slowing down the tests.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                SchedulingConfiguration.class,
                SpringSchedulerTest.TestContext.class
        },
        initializers = ConfigDataApplicationContextInitializer.class // Loads application properties
)
@MockBeansMatching(
        @ComponentScan(
                basePackageClasses = BackityApplication.class,
                includeFilters = @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = GameProviderServiceConfiguration.class
                ),
                useDefaultFilters = false
        )
)
public @interface SpringSchedulerTest {

    @TestConfiguration
    @ComponentScan(
            basePackageClasses = BackityApplication.class,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = SpringSchedulerBeanConfiguration.class
            ),
            useDefaultFilters = false
    )
    class TestContext {

        /// Ensures scheduled tasks don't execute in the background
        @Bean
        TaskScheduler noOpTaskScheduler() {
            return new NoOpTaskScheduler();
        }

        @Bean
        RegisteredSchedulers registeredSchedulers(ScheduledTaskHolder scheduledTaskHolder) {
            return new RegisteredSchedulers(scheduledTaskHolder);
        }
    }
}
