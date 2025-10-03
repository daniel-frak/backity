package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
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
        FileBackupFinishedEventHandler.class,
        BackupRecoveryCompletedEventHandler.class,
        FileCopyReplicationProgressChangedEventHandler.class,
        DomainEventForwardingHandler.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SpringEventListenerTestBeans {
}
