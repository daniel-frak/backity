package dev.codesoapbox.backity.testing.messaging.outbox;

import dev.codesoapbox.backity.testing.async.TrackingTaskExecutor;
import org.awaitility.Awaitility;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class OutboxEventScenario {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final ApplicationEventPublisher publisher;
    private final PlatformTransactionManager transactionManager;
    private final EventSerializer eventSerializer;
    private final TrackingTaskExecutor trackingTaskExecutor;

    public OutboxEventScenario(ApplicationEventPublisher publisher,
                               PlatformTransactionManager transactionManager,
                               EventSerializer eventSerializer, TrackingTaskExecutor trackingTaskExecutor) {
        this.publisher = publisher;
        this.transactionManager = transactionManager;
        this.eventSerializer = eventSerializer;
        this.trackingTaskExecutor = trackingTaskExecutor;
    }

    public PublishStage publish(Object event) {
        return new PublishStage(event);
    }

    public final class PublishStage {

        private final Object event;

        private PublishStage(Object event) {
            this.event = event;
        }

        @SuppressWarnings("java:S5977") // ExecutorClientId UUID must be randomly generated
        public void thenVerifyAsync(Runnable assertion) {
            UUID executorClientId = UUID.randomUUID();
            trackingTaskExecutor.reset();
            trackingTaskExecutor.setExecutorClientId(executorClientId);

            assertSerdesWorksCorrectly(event);

            runWithinNewTransaction(() -> {
                publisher.publishEvent(event);
                verifyDidNotRunBeforeTransactionCommitted(assertion);
            });

            Awaitility.await()
                    .atMost(TIMEOUT)
                    .untilAsserted(assertion::run);

            assertEventHandlerWasExecutedAsynchronously(executorClientId);
        }

        /*
        After publishing the event to the outbox table, the domain event gets sent directly to the listener.
        There seems to be no easy way to make Spring deserialize the event before sending it to the listener.
        This assertion is a workaround to make sure event deserialization works correctly.
         */
        private void assertSerdesWorksCorrectly(Object event) {
            Object outboxEvent = eventSerializer.serialize(event);
            Object deserializedEvent = eventSerializer.deserialize(outboxEvent, event.getClass());

            assertThat(deserializedEvent)
                    .as("Event deserialization failed")
                    .usingRecursiveComparison()
                    .isEqualTo(event);
        }

        private void runWithinNewTransaction(Runnable runnable) {
            var template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            template.executeWithoutResult(_ -> runnable.run());
        }

        private void verifyDidNotRunBeforeTransactionCommitted(Runnable assertion) {
            assertThatCode(assertion::run)
                    .withFailMessage(
                            "Event Handler ran before the event-emitting transaction was committed. " +
                                    "Did you use `@EventListener` instead of `@TransactionalEventListener`?")
                    .isInstanceOf(Throwable.class);
        }

        private void assertEventHandlerWasExecutedAsynchronously(UUID executorClientId) {
            assertThat(trackingTaskExecutor.wasUsedFor(executorClientId))
                    .withFailMessage("Event Handler was not executed asynchronously. " +
                            "Did you forget to add `@Async`?")
                    .isTrue();
        }
    }
}