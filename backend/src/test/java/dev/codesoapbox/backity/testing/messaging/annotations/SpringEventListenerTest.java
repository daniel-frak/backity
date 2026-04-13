package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.SpringEventSerializationBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.support.CompletionMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

import java.lang.annotation.*;

/// Annotation for a domain event listener.
///
/// Supports {@link TransactionalEventListener}s through an in-memory {@link EventPublicationRepository}.
///
/// **Caveat:** Transaction rollback is NOT supported by the in-memory {@link EventPublicationRepository}.
///
/// # Motivation for shared context
///
/// While creating an application context for event handler tests does not take a long time, it can lead to
/// the context cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
///
/// Thus, making all domain event handler tests share a single application context should protect against
/// cache eviction slowing down the tests.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringEventListenerTest.TestContext.class)
@Import({
        SpringEventSerializationBeanConfig.class
})
@EnableTransactionManagement
@EnablePersistentDomainEvents
@MockitoBean(types = {
        // `@EnablePersistentDomainEvents` automatically adds JpaEventPublicationRepository, which we can't exclude
        // since it's a protected class. We mock EntityManager because it needs it.
        EntityManager.class,

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
    @Import({SpringEventSerializationBeanConfig.class})
    @TestConfiguration
    class TestContext {

        @Bean
        PlatformTransactionManager platformTransactionManager() {
            return new DummyPlatformTransactionManager();
        }

        // @TODO Is this necessary?
        @Bean
        JsonMapper jsonMapper() {
            return JsonMapper.builder()
                    .build();
        }

        @Bean
        @Primary
        EventPublicationRepository eventPublicationRepository(EventSerializer serializer) {
            return new InMemoryEventPublicationRepository(serializer, CompletionMode.UPDATE);
        }
    }
}