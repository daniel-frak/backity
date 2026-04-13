package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.exceptions.OutboxEventMapperNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnotationBasedJsonEventSerializerTest {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @Mock
    private AnnotationBasedOutboxEventMapperRegistry eventMapperRegistry;

    private AnnotationBasedJsonEventSerializer eventSerializer;

    @BeforeEach
    void setUp() {
        eventSerializer = new AnnotationBasedJsonEventSerializer(JSON_MAPPER, eventMapperRegistry);
    }

    @SuppressWarnings({"SameParameterValue", "unchecked", "rawtypes"})
    private void mapperExists(
            OutboxEventMapperAdapter<TestDomainEvent, TestOutboxEvent> mapperAdapter) {
        when(eventMapperRegistry.getByDomainType(TestDomainEvent.class))
                .thenReturn(Optional.of((OutboxEventMapperAdapter) mapperAdapter));
    }

    @SuppressWarnings("unchecked")
    private OutboxEventMapperAdapter<TestDomainEvent, TestOutboxEvent>
    aMapperAdapterMock() {
        return mock(OutboxEventMapperAdapter.class);
    }

    private OutboxEventMapperAdapter<TestDomainEvent, TestOutboxEvent>
    aMapperAdapter(TestDomainEvent domainEvent, TestOutboxEvent outboxEvent) {
        OutboxEventMapperAdapter<TestDomainEvent, TestOutboxEvent> mapperAdapter =
                aMapperAdapterMock();
        lenient().when(mapperAdapter.getOutboxType())
                .thenReturn(TestOutboxEvent.class);
        lenient().when(mapperAdapter.toOutbox(domainEvent))
                .thenReturn(outboxEvent);
        lenient().when(mapperAdapter.toDomain(outboxEvent))
                .thenReturn(domainEvent);

        return mapperAdapter;
    }

    private void noMappersExist() {
        when(eventMapperRegistry.getByDomainType(any()))
                .thenReturn(Optional.empty());
    }

    private void aMapperExists(TestDomainEvent domainEvent, TestOutboxEvent outboxEvent) {
        OutboxEventMapperAdapter<TestDomainEvent, TestOutboxEvent> mapperAdapter =
                aMapperAdapter(domainEvent, outboxEvent);
        mapperExists(mapperAdapter);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertJsonEquals(Object result, String expectedResult) {
        assertThat(JSON_MAPPER.readTree(result.toString())).isEqualTo(JSON_MAPPER.readTree(expectedResult));
    }

    private record TestDomainEvent(String data) {
    }

    private record TestOutboxEvent(String data) {
    }

    @Nested
    class Serialize {

        @Test
        void shouldSerializeDomainEventToOutboxJson() {
            var domainEvent = new TestDomainEvent("test-data");
            var outboxEvent = new TestOutboxEvent("test-data-mapped");
            aMapperExists(domainEvent, outboxEvent);

            Object result = eventSerializer.serialize(domainEvent);

            assertJsonEquals(result, """
                    {
                        "data":"test-data-mapped"
                    }
                    """);
        }

        @Test
        void shouldThrowExceptionGivenNoMapperFoundDuringSerialization() {
            var domainEvent = new TestDomainEvent("test-data");

            noMappersExist();

            assertThatThrownBy(() -> eventSerializer.serialize(domainEvent))
                    .isInstanceOf(OutboxEventMapperNotFoundException.class);
        }
    }

    @Nested
    class Deserialize {

        @Test
        void shouldDeserializeOutboxJsonToDomainEvent() {
            var domainEvent = new TestDomainEvent("test-data");
            var outboxEvent = new TestOutboxEvent("test-data-mapped");
            aMapperExists(domainEvent, outboxEvent);
            String outboxJson = """
                    {
                        "data":"test-data-mapped"
                    }
                    """;

            TestDomainEvent result = eventSerializer.deserialize(outboxJson, TestDomainEvent.class);

            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(domainEvent);
        }

        @Test
        void shouldThrowExceptionGivenNoMapperFoundDuringDeserialization() {
            String outboxJson = "{}";

            noMappersExist();

            assertThatThrownBy(() -> eventSerializer.deserialize(outboxJson, TestDomainEvent.class))
                    .isInstanceOf(OutboxEventMapperNotFoundException.class);
        }
    }
}