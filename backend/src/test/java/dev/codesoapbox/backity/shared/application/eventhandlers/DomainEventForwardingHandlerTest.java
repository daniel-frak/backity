package dev.codesoapbox.backity.shared.application.eventhandlers;

import dev.codesoapbox.backity.shared.application.eventhandlers.exceptions.DomainEventForwardingHandlerException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;

class DomainEventForwardingHandlerTest {

    @Test
    void shouldForwardEventsGivenConsumersExist() {
        var eventConsumer = new TestEventConsumer();
        var eventHandler = new DomainEventForwardingHandler(Map.of(
                TestEvent.class, List.of(eventConsumer)
        ));
        TestEvent event = new TestEvent("test");

        eventHandler.handle(event);

        assertThat(eventConsumer.getForwardedEvents()).containsExactly(event.value());
    }

    @Test
    void shouldDoNothingGivenConsumersAreNull() {
        var eventHandler = new DomainEventForwardingHandler(Map.of());
        TestEvent event = new TestEvent("test");

        assertThatCode(() -> eventHandler.handle(event))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldDoNothingGivenConsumersAreEmpty() {
        var eventHandler = new DomainEventForwardingHandler(Map.of(
                TestEvent.class, emptyList()
        ));
        TestEvent event = new TestEvent("test");

        assertThatCode(() -> eventHandler.handle(event))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldWrapButDeliverLaterGivenEarlierConsumerThrows() {
        var throwingConsumer = new TestEventConsumer();
        RuntimeException expectedCause = new RuntimeException("Test exception");
        throwingConsumer.setShouldThrow(expectedCause);
        var succeedingConsumer = new TestEventConsumer();
        var eventHandler = new DomainEventForwardingHandler(Map.of(
                TestEvent.class, List.of(throwingConsumer, succeedingConsumer)
        ));
        TestEvent event = new TestEvent("test");

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(DomainEventForwardingHandlerException.class)
                .hasSuppressedException(expectedCause);
        assertThat(succeedingConsumer.getForwardedEvents()).containsExactly(event.value());
    }

    @Test
    void shouldDoNothingGivenNoForwarder() {
        var eventConsumer = new TestEventConsumer();
        var eventHandler = new DomainEventForwardingHandler(Map.of(
                TestEvent.class, List.of(eventConsumer)
        ));
        Object event = new Object();

        eventHandler.handle(event);

        assertThat(eventConsumer.getForwardedEvents()).isEmpty();
    }

    private record TestEvent(String value) {
    }

    private static class TestEventConsumer implements DomainEventForwardingHandler.EventForwardingConsumer<TestEvent> {

        @Getter
        private final List<String> forwardedEvents = new ArrayList<>();

        @Setter
        private RuntimeException shouldThrow = null;

        @Override
        public void accept(TestEvent testEvent) {
            if (shouldThrow != null) {
                throw shouldThrow;
            }
            forwardedEvents.add(testEvent.value());
        }
    }
}