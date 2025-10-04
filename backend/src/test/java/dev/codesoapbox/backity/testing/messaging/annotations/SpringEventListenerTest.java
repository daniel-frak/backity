package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Annotation for a domain event listener.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for event handler tests does not take a long time, it can lead to
 * the context cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all domain event handler tests share a single application context should protect against
 * cache eviction slowing down the tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringEventListenerTest.TestContext.class)
@MockitoBean(types = {
        FileBackupFinishedEventHandler.class,
        BackupRecoveryCompletedEventHandler.class,
        FileCopyReplicationProgressChangedEventHandler.class,
        DomainEventForwardingHandler.class
})
public @interface SpringEventListenerTest {

    @ComponentScan(
            basePackageClasses = BackityApplication.class,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {
                            SpringEventListenerBeanConfiguration.class,
                            SpringApplicationEventPublisherBeanConfiguration.class
                    }
            ),
            useDefaultFilters = false
    )
    @TestConfiguration
    class TestContext {
    }
}
