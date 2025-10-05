package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.shared.application.events.outbox.OutboxEvent;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
abstract class DomainEventOutboxJpaRepositoryAbstractIT {

    private static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;

    @Autowired
    private Clock clock;

    @Autowired
    private DomainEventOutboxSpringRepository springRepository;

    @Autowired
    private SpringPageMapper pageMapper;

    @Autowired
    private SpringPageableMapper paginationMapper;

    private DomainEventOutboxJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DomainEventOutboxJpaRepository(clock, springRepository, pageMapper, paginationMapper,
                Map.of(
                        TestDomainEvent.class,
                        new TestDomainEventSerializer()
                )
        );
    }

    @Test
    void shouldSaveEvent() {
        UUID outboxEventId = UUID.fromString("c0ce9378-e776-4a1b-b6aa-8e6ee560f195");
        var event = new TestDomainEvent("serializedValue");
        var outboxEvent = new OutboxEvent(outboxEventId, event);

        repository.save(outboxEvent);

        List<OutboxEventEntity> allEvents = springRepository.findAll();

        var expectedEvent = new OutboxEventEntity(
                outboxEventId,
                TestDomainEvent.class.getName(),
                Map.of("value", "serializedValue"),
                false,
                NOW
        );
        assertThat(allEvents.size()).isOne();
        assertThat(allEvents.getFirst()).usingRecursiveComparison()
                .isEqualTo(expectedEvent);
    }

    @Test
    void shouldReturnLatestUnprocessedEvents() {
        var outboxEvent0 = new OutboxEventEntity(
                UUID.fromString("d301e74e-6926-42a8-9484-71adb6aba586"),
                TestDomainEvent.class.getName(),
                Map.of("value", "1"),
                true,
                NOW.minusHours(3)
        );
        var outboxEvent1 = new OutboxEventEntity(
                UUID.fromString("c0ce9378-e776-4a1b-b6aa-8e6ee560f195"),
                TestDomainEvent.class.getName(),
                Map.of("value", "1"),
                false,
                NOW.minusHours(3)
        );
        var outboxEvent2 = new OutboxEventEntity(
                UUID.fromString("9d0f8547-b289-40b8-9da2-ba2c0d1032ef"),
                TestDomainEvent.class.getName(),
                Map.of("value", "2"),
                false,
                NOW.minusHours(2)
        );
        var outboxEvent3 = new OutboxEventEntity(
                UUID.fromString("f9311599-a42a-4e90-aedf-577945dab7c8"),
                TestDomainEvent.class.getName(),
                Map.of("value", "3"),
                false,
                NOW.minusHours(1)
        );
        springRepository.saveAll(List.of(outboxEvent3, outboxEvent2, outboxEvent1, outboxEvent0));
        springRepository.flush();

        Page<OutboxEvent> result = repository.findAllUnprocessedOrderedByCreatedAtAsc(new Pagination(0, 2));

        var expectedEvent1 = new OutboxEvent(outboxEvent1.getId(), new TestDomainEvent("1"));
        var expectedEvent2 = new OutboxEvent(outboxEvent2.getId(), new TestDomainEvent("2"));
        assertThat(result.content()).containsExactly(expectedEvent1, expectedEvent2);
    }

    record TestDomainEvent(
            String value
    ) implements DomainEvent {
    }

    private static class TestDomainEventSerializer implements DomainEventOutboxJpaSerializer<TestDomainEvent> {

        @Override
        public Map<String, Object> serialize(TestDomainEvent event) {
            return Map.of(
                    "value", event.value()
            );
        }

        @Override
        public TestDomainEvent deserialize(Map<String, Object> eventData) {
            return new TestDomainEvent((String) eventData.get("value"));
        }

        @Override
        public Class<TestDomainEvent> getSupportedEventClass() {
            return TestDomainEvent.class;
        }
    }
}
