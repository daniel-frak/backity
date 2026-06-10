package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationListenerBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import dev.codesoapbox.backity.testing.messaging.extensions.ApplicationEventScenarioExtension;
import dev.codesoapbox.backity.testing.mocking.MockBeansMatching;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;
import java.time.Duration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SpringApplicationListenerTest.TestContext.class
})
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
@ExtendWith(ApplicationEventScenarioExtension.class)
public @interface SpringApplicationListenerTest {

    @ComponentScan(
            basePackageClasses = BackityApplication.class,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {
                            SpringApplicationListenerBeanConfiguration.class
                    }
            ),
            useDefaultFilters = false
    )
    @TestConfiguration
    class TestContext {

        /// Normally, the event is fired by `EventPublishingRunListener`, but that class is private so we can't use it.
        @Bean
        SmartInitializingSingleton applicationReadyEventPublisher(ConfigurableApplicationContext context) {
            return () -> context.publishEvent(
                    new ApplicationReadyEvent(
                            new SpringApplication(BackityApplication.class),
                            new String[]{},
                            context,
                            Duration.ZERO
                    )
            );
        }
    }
}