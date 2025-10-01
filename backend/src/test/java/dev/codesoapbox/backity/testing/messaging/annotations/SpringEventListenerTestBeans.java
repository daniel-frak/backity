package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.*;
import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryProgressChangedEventHandler;
import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStartedEventHandler;
import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStoppedEventHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.SpringDomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.*;

@Import({
        SpringDomainEventPublisherBeanConfig.class
})
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = SpringEventListenerBeanConfiguration.class
        ),
        useDefaultFilters = false
)
@MockitoBean(types = {
        FileBackupStartedEventHandler.class,
        FileBackupFinishedEventHandler.class,
        FileBackupFailedEventHandler.class,
        BackupRecoveryCompletedEventHandler.class,
        FileCopyReplicationProgressChangedEventHandler.class,
        GameContentDiscoveryStartedEventHandler.class,
        GameContentDiscoveryProgressChangedEventHandler.class,
        GameContentDiscoveryStoppedEventHandler.class,
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SpringEventListenerTestBeans {
}
