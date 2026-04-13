package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import java.lang.annotation.*;

/// Marks a component responsible for converting a domain event into its corresponding outbox DTO representation.
///
/// # Usage example
/// 
/// 1. Create an outbox DTO for your event in a `*.infrastructure.adapters.shared.messaging.spring.outbox` package:
/// ```
/// public record YourOutboxEvent(
///         // ...
/// ) {
/// }
/// ```
///
/// 2. Declare a mapper in a `*.infrastructure.adapters.shared.messaging.spring.outbox` package:
/// ```
/// @OutboxEventMapper(
///         domain = YourEvent.class,
///         outbox = YourOutboxEvent.class
/// )
/// @Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
/// public interface YourOutboxEventMapper {
///
///     YourOutboxEvent toOutbox(YourEvent domain);
///
///     YourEvent toDomain(YourOutboxEvent outbox);
/// }
/// ```
/// 
/// 3. Declare it in a `@SpringEventListenerBeanConfiguration` class:
/// ```
/// @SpringEventListenerBeanConfiguration
/// public class YourEventSpringListenerBeanConfig {
///
///     // ...
///
///     @Bean
///     YourOutboxEventMapper yourOutboxEventMapper() {
///         return Mappers.getMapper(YourOutboxEventMapper.class);
///     }
/// }
/// ```
///
/// 4. Annotate the listener method with `@Async @TransactionalEventListener(id="your-event-listener-id")`:
/// ```
/// @Async
/// @TransactionalEventListener(id = "your-event-listener-id")
/// public void listen(YourEvent event) { ... }
/// ```
///
/// 5. Test the listener using `@SpringEventListenerTest` and `OutboxEventScenario`:
/// ```
/// @SpringEventListenerTest
/// class PerformActionOnYourEventSpringListenerIT {
/// 
///     @Autowired
///     private PerformActionOnYourEventHandler eventHandler;
/// 
///     @Test
///     void shouldHandleEvent(OutboxEventScenario scenario) {
///         YourEvent event = TestYourEvent.finishedIntegrityUnknown();
/// 
///         scenario.publish(event)
///                 .thenVerifyAsync(() -> verify(eventHandler).handle(event));
///     }
/// }
/// ```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OutboxEventMapper {

    /// @return the class type of the domain event.
    Class<?> domain();

    /// @return the class type of the outbox event.
    Class<?> outbox();
}