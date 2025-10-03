package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwarder;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.events.DomainEventForwarderFinder;
import dev.codesoapbox.backity.shared.infrastructure.config.events.exceptions.DomainEventForwarderException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class DomainEventForwarderFinderIT {

    @Autowired
    private DomainEventForwarderFinder finder;

    @Autowired
    private TestEventForwarder testEventForwarder;

    @BeforeEach
    void setUp() {
        testEventForwarder.clear();
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldFindEventForwardingConsumersByEventClass() {
        var event = new TestEvent("test");

        Map<Class<?>, List<DomainEventForwardingHandler.EventForwardingConsumer<?>>> result =
                finder.findEventForwardingConsumersByEventClass();

        List<DomainEventForwardingHandler.EventForwardingConsumer<?>> forwardingConsumers = result.get(TestEvent.class);
        for (DomainEventForwardingHandler.EventForwardingConsumer<?> forwardingConsumer : forwardingConsumers) {
            ((DomainEventForwardingHandler.EventForwardingConsumer<TestEvent>) forwardingConsumer).accept(event);
        }

        assertThat(testEventForwarder.getForwardedEvents()).containsExactly("test");
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldWrapGivenEventConsumerThrows() {
        var event = new TestEvent("test");
        var expectedCause = new RuntimeException("Test exception");
        testEventForwarder.setShouldThrow(expectedCause);
        Map<Class<?>, List<DomainEventForwardingHandler.EventForwardingConsumer<?>>> result =
                finder.findEventForwardingConsumersByEventClass();
        List<DomainEventForwardingHandler.EventForwardingConsumer<?>> forwardingConsumers = result.get(TestEvent.class);
        var eventConsumer =
                (DomainEventForwardingHandler.EventForwardingConsumer<TestEvent>) forwardingConsumers.getFirst();

        assertThatThrownBy(() -> eventConsumer.accept(event))
                .isInstanceOf(DomainEventForwarderException.class)
                .cause() // InvocationTargetException
                .hasCause(expectedCause);
    }

    public record TestEvent(String value) {
    }

    @DomainEventForwarder
    public static class TestEventForwarder {

        @Setter
        private RuntimeException shouldThrow = null;

        @Getter
        private final List<String> forwardedEvents = new ArrayList<>();

        public void clear() {
            forwardedEvents.clear();
        }

        @SuppressWarnings("unused") // This method should be automatically found
        public void forward(TestEvent testEvent) {
            if (shouldThrow != null) {
                throw shouldThrow;
            }
            forwardedEvents.add(testEvent.value());
        }
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        DomainEventForwarderFinder finder(ApplicationContext applicationContext) {
            return new DomainEventForwarderFinder(applicationContext);
        }

        @Bean
        TestEventForwarder testEventForwarder() {
            return new TestEventForwarder();
        }
    }
}