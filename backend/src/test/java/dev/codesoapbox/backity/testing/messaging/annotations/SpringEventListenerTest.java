package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.ClearProgressOnFileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.application.eventhandlers.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringAsyncConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import dev.codesoapbox.backity.testing.async.TrackingTaskExecutor;
import dev.codesoapbox.backity.testing.jpa.annotations.PostgresRepositoryTest;
import dev.codesoapbox.backity.testing.messaging.extensions.InMemoryEventScenarioExtension;
import dev.codesoapbox.backity.testing.messaging.extensions.OutboxEventScenarioExtension;
import dev.codesoapbox.backity.testing.messaging.outbox.CleanUpOutboxEventsAfterTestExtension;
import dev.codesoapbox.backity.testing.messaging.outbox.OutboxEventScenario;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.jpa.updating.DefaultJpaEventPublication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

import java.lang.annotation.*;

/// Configures a Spring test slice for asserting on domain event listeners.
///
/// Supports testing of [TransactionalEventListener] methods
/// by injecting an [OutboxEventScenario] into the test method.
///
/// # Note - this uses [PostgresRepositoryTest]
///
/// Some Spring Listeners are annotated with [TransactionalEventListener], which means the events are sent to
/// an outbox table via `JpaEventPublicationRepository`. This requires a working connection to a database.
///
/// The major caveat is that the database instance is shared between all tests.
/// This increases the risk of test flakiness due to a test slice not properly cleaning up after itself, breaking
/// another test slice.
///
/// An alternative would be to write a custom in-memory [EventPublicationRepository] implementation.
/// However, that interface has a lot of methods to implement, and would be an additional maintenance burden.
/// It would also need a dummy [PlatformTransactionManager] to trigger TransactionalEventListeners after commit
/// Additionally, the benefits of the current approach are that:
/// - It also tests that the event_publication schema is created correctly,
/// - Transaction rollback is supported out-of-the-box.
///
/// # Motivation for shared context
///
/// Creating many unique Spring Boot contexts can lead to the context cache filling up and evicting other,
/// equally expensive contexts.
///
/// The database connection pool is also likely to run out of connections,
/// randomly failing tests with errors such as "PSQLException: FATAL: sorry, too many clients already".
///
/// Thus, making all repository tests share a single application context should protect against cache eviction slowing
/// down the tests, as well as test flakiness due to connection issues.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SpringEventListenerTest.TestContext.class)
@EnablePersistentDomainEvents // Without this, events will not be automatically persisted to the outbox table
@TestPropertySource(properties = {
        // Otherwise will throw `This ResultSet is closed` PSQLException:
        "spring.modulith.events.republish-outstanding-events-on-restart=false"
})
@MockitoBean(types = {
        ClearProgressOnFileBackupFinishedEventHandler.class,
        MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler.class,
        SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler.class,
        DomainEventForwardingHandler.class
})
@ExtendWith(OutboxEventScenarioExtension.class)
@ExtendWith(InMemoryEventScenarioExtension.class)
@ExtendWith(CleanUpOutboxEventsAfterTestExtension.class)
@PostgresRepositoryTest
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {
                        SpringAsyncConfiguration.class,
                        SpringEventListenerBeanConfiguration.class,
                        SpringApplicationEventPublisherBeanConfiguration.class
                }
        ),
        useDefaultFilters = false
)
@EntityScan(basePackageClasses = {
        BackityApplication.class,
        DefaultJpaEventPublication.class
})
public @interface SpringEventListenerTest {

    @TestConfiguration
    class TestContext {

        @Bean
        JsonMapper jsonMapper() {
            return JsonMapper.builder()
                    .build();
        }

        @Primary
        @Bean
        TrackingTaskExecutor trackingTaskExecutor() {
            return new TrackingTaskExecutor(new SimpleAsyncTaskExecutor());
        }
    }
}