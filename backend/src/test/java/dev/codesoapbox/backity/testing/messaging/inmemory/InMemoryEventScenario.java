package dev.codesoapbox.backity.testing.messaging.inmemory;

import dev.codesoapbox.backity.testing.async.TrackingTaskExecutor;
import org.awaitility.Awaitility;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryEventScenario {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final ApplicationEventPublisher publisher;
    private final TrackingTaskExecutor trackingTaskExecutor;

    public InMemoryEventScenario(ApplicationEventPublisher publisher, TrackingTaskExecutor trackingTaskExecutor) {
        this.publisher = publisher;
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

        public void thenVerify(Runnable assertion) {
            boolean shouldNotBeAsync = false;
            thenVerify(assertion, shouldNotBeAsync);
        }

        public void thenVerifyAsync(Runnable assertion) {
            boolean shouldBeAsync = true;
            thenVerify(assertion, shouldBeAsync);
        }

        @SuppressWarnings("java:S5977") // ExecutorClientId UUID must be randomly generated
        private void thenVerify(Runnable assertion, boolean shouldBeAsync) {
            UUID executorClientId = UUID.randomUUID();
            trackingTaskExecutor.reset();
            trackingTaskExecutor.setExecutorClientId(executorClientId);

            publisher.publishEvent(event);

            Awaitility.await()
                    .atMost(TIMEOUT)
                    .untilAsserted(assertion::run);

            if(shouldBeAsync) {
                assertEventHandlerWasExecutedAsynchronously(executorClientId);
            } else {
                assertEventHandlerWasExecutedSynchronously(executorClientId);
            }
        }

        private void assertEventHandlerWasExecutedAsynchronously(UUID executorClientId) {
            assertThat(trackingTaskExecutor.wasUsedFor(executorClientId))
                    .withFailMessage("Event Handler was not executed asynchronously. " +
                            "Did you forget to add `@Async`?")
                    .isTrue();
        }

        private void assertEventHandlerWasExecutedSynchronously(UUID executorClientId) {
            assertThat(trackingTaskExecutor.wasUsedFor(executorClientId))
                    .withFailMessage("Event Handler was executed asynchronously. " +
                            "Use `verifyAsync` if this is the intended behavior.")
                    .isFalse();
        }
    }
}