package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnnotationBasedOutboxEventMapperRegistryTest {

    private AnnotationBasedOutboxEventMapperRegistry aRegistryWithAValidMapper() {
        var validMapper = new ValidMapper();
        List<Object> mappers = List.of(validMapper);

        return new AnnotationBasedOutboxEventMapperRegistry(mappers);
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class ValidMapper {

        public TestOutboxEvent toOutbox(TestDomainEvent domainEvent) {
            return new TestOutboxEvent(domainEvent.value());
        }

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            return new TestDomainEvent(outboxEvent.data());
        }
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class MapperWithMissingMethod {

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            return new TestDomainEvent(outboxEvent.data());
        }

        // Missing toOutbox method
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class MapperWithWrongReturnType {

        // Wrong - return type should be TestOutboxEvent
        public String toOutbox(TestDomainEvent domainEvent) {
            return domainEvent.value();
        }

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            return new TestDomainEvent(outboxEvent.data());
        }
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class MapperWithWrongParameterType {

        // Wrong - parameter should be TestDomainEvent
        public TestOutboxEvent toOutbox(String input) {
            return new TestOutboxEvent(input);
        }

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            return new TestDomainEvent(outboxEvent.data());
        }
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class MapperWithWrongParameterCount {

        // Wrong - 2 parameters instead of 1
        @SuppressWarnings({"unused", "java:S1172"})
        public TestOutboxEvent toOutbox(TestDomainEvent domainEvent, String extra) {
            return new TestOutboxEvent(domainEvent.value());
        }

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            return new TestDomainEvent(outboxEvent.data());
        }
    }

    @OutboxEventMapper(
            domain = TestDomainEvent.class,
            outbox = TestOutboxEvent.class
    )
    static class ThrowingMapper {

        static final RuntimeException TO_OUTBOX_EXCEPTION = new RuntimeException("toOutbox exception");
        static final RuntimeException TO_DOMAIN_EXCEPTION = new RuntimeException("toDomain exception");

        public TestOutboxEvent toOutbox(TestDomainEvent domainEvent) {
            throw TO_OUTBOX_EXCEPTION;
        }

        public TestDomainEvent toDomain(TestOutboxEvent outboxEvent) {
            throw TO_DOMAIN_EXCEPTION;
        }
    }

    record TestDomainEvent(String value) {
    }

    record TestOutboxEvent(String data) {
    }

    @Nested
    class Constructor {

        @Test
        void shouldRegisterValidMapper() {
            var mapper = new ValidMapper();
            List<Object> mappers = List.of(mapper);

            var registry = new AnnotationBasedOutboxEventMapperRegistry(mappers);

            assertThat(registry.getByDomainType(TestDomainEvent.class))
                    .isNotNull();
        }

        @Test
        void shouldThrowExceptionGivenAnnotationIsMissing() {
            var mapperWithoutAnnotation = new Object();
            List<Object> mappers = List.of(mapperWithoutAnnotation);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No @OutboxEventMapper annotation found on " + Object.class.getName());
        }

        @Test
        void shouldThrowExceptionGivenMappingMethodIsMissing() {
            var mapperWithMissingMethod = new MapperWithMissingMethod();
            List<Object> mappers = List.of(mapperWithMissingMethod);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No method mapping TestDomainEvent -> TestOutboxEvent " +
                            "found in " + MapperWithMissingMethod.class.getName());
        }

        @Test
        void shouldThrowExceptionGivenOnlyMethodWithWrongReturnTypeExists() {
            var mapper = new MapperWithWrongReturnType();
            List<Object> mappers = List.of(mapper);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No method mapping TestDomainEvent -> TestOutboxEvent found in "
                            + MapperWithWrongReturnType.class.getName());
        }

        @Test
        void shouldThrowExceptionGivenMethodWithWrongParameterTypeExists() {
            var mapper = new MapperWithWrongParameterType();
            List<Object> mappers = List.of(mapper);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No method mapping TestDomainEvent -> TestOutboxEvent found in "
                            + MapperWithWrongParameterType.class.getName());
        }

        @Test
        void shouldThrowExceptionGivenMethodWithWrongParameterCountExists() {
            var mapper = new MapperWithWrongParameterCount();
            List<Object> mappers = List.of(mapper);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No method mapping TestDomainEvent -> TestOutboxEvent found in "
                            + MapperWithWrongParameterCount.class.getName());
        }

        @Test
        void shouldThrowExceptionGivenDuplicateMappersExistForSameDomainType() {
            var firstMapper = new ValidMapper();
            var secondMapper = new ValidMapper();
            List<Object> mappers = List.of(firstMapper, secondMapper);

            assertThatThrownBy(() -> new AnnotationBasedOutboxEventMapperRegistry(mappers))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Duplicate OutboxEventMapper for type: " + TestDomainEvent.class.getName());
        }

        @Test
        void shouldPropagateExceptionFromAdapterToOutbox() {
            var mapper = new ThrowingMapper();
            var registry = new AnnotationBasedOutboxEventMapperRegistry(List.of(mapper));
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            OutboxEventMapperAdapter<TestDomainEvent, Object> adapter =
                    registry.getByDomainType(TestDomainEvent.class).get();

            var domainEvent = new TestDomainEvent("test");

            assertThatThrownBy(() -> adapter.toOutbox(domainEvent))
                    .isSameAs(ThrowingMapper.TO_OUTBOX_EXCEPTION);
        }

        @Test
        void shouldPropagateExceptionFromAdapterToDomain() {
            var mapper = new ThrowingMapper();
            var registry = new AnnotationBasedOutboxEventMapperRegistry(List.of(mapper));
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            OutboxEventMapperAdapter<TestDomainEvent, Object> adapter =
                    registry.getByDomainType(TestDomainEvent.class).get();

            var outboxEvent = new TestOutboxEvent("test");

            assertThatThrownBy(() -> adapter.toDomain(outboxEvent))
                    .isSameAs(ThrowingMapper.TO_DOMAIN_EXCEPTION);
        }
    }

    @Nested
    class Mapping {

        @Nested
        class ToOutbox {

            @Test
            void shouldMapFromDomainToOutboxGivenRegistryContainsValidMapper() {
                AnnotationBasedOutboxEventMapperRegistry registry = aRegistryWithAValidMapper();
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                OutboxEventMapperAdapter<TestDomainEvent, Object> adapter =
                        registry.getByDomainType(TestDomainEvent.class).get();
                var domainEvent = new TestDomainEvent("test-data");

                Object result = adapter.toOutbox(domainEvent);

                assertThat(result)
                        .isInstanceOf(TestOutboxEvent.class);
                assertThat(((TestOutboxEvent) result).data())
                        .isEqualTo("test-data");
            }
        }

        @Nested
        class ToDomain {

            @Test
            void shouldMapFromOutboxToDomainGivenRegistryContainsValidMapper() {
                AnnotationBasedOutboxEventMapperRegistry registry = aRegistryWithAValidMapper();
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                OutboxEventMapperAdapter<TestDomainEvent, Object> adapter =
                        registry.getByDomainType(TestDomainEvent.class).get();
                var outboxEvent = new TestOutboxEvent("test-data");

                TestDomainEvent result = adapter.toDomain(outboxEvent);

                assertThat(result.value())
                        .isEqualTo("test-data");
            }
        }

        @Nested
        class GetOutboxType {

            @Test
            void shouldReturnCorrectOutboxType() {
                AnnotationBasedOutboxEventMapperRegistry registry = aRegistryWithAValidMapper();
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                OutboxEventMapperAdapter<TestDomainEvent, Object> adapter =
                        registry.getByDomainType(TestDomainEvent.class).get();

                var outboxType = adapter.getOutboxType();

                assertThat(outboxType)
                        .isEqualTo(TestOutboxEvent.class);
            }
        }
    }
}