---
title: 0009 - Decouple event listeners from handlers
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

Event handling is the responsibility of the application layer, 
as only that layer has the context to orchestrate and aggregate events into meaningful behavior.
However, the only officially supported way to handle events in Spring Boot is through the `@EventListener` annotation.
Using this annotation would couple the application layer to the Spring framework.

A custom application-layer annotation like `@DomainEventHandler` could be written to decouple the application layer
from the Spring framework. An infrastructure-layer inbound adapter could then dynamically register event listeners
based on the presence of this annotation.
However, Spring currently does not provide an easy way to programmatically register
event listeners, while writing a custom equivalent of `EventListenerMethodProcessor` would be a significant maintenance
burden.

Until Spring provides a better way to programmatically register event listeners, the most straightforward solution is to
write Spring-based event listeners as inbound adapters, with separate event handlers kept in the application layer.
While this increases the probability of forgetting to register an event listener, 
that can be mitigated by writing an ArchUnit rule.

## Decision

- Write Spring-based event listeners as inbound adapters
- Write framework-agnostic event handlers in the application layer.
- Trigger event handlers from event listeners as their only action.
- Write an ArchUnit rule to ensure that all event handlers have corresponding event listeners.

## Consequences

### Positive

- The application layer is decoupled from the framework, due to no dependency on `@EventListener`.
- Cleaner separation in testing: listeners are tested using integration tests while handlers are tested using unit
  tests.

### Negative

- More boilerplate code due to having to write a separate event listener for each event handler.