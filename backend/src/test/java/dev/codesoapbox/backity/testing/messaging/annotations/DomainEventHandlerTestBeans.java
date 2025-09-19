package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.config.FileBackupEventHandlerBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.DomainEventPublisherBeanConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.*;

@Import({
        DomainEventPublisherBeanConfig.class,

        // Project - specific
        FileBackupEventHandlerBeanConfig.class
})
@MockitoBean(types = {
        FileCopyReplicationProgressRepository.class,
        FileCopyReplicationProcess.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DomainEventHandlerTestBeans {
}
