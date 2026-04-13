---
title: Use domain events
status: accepted
scope: backend
tags: [ 'architecture' ]
---

## Context

The process of discovering and backing up game files involves multiple asynchronous operations. Game files need to be
discovered from different providers, validated, and backed up, all of which can happen independently.
Moreover, the backend must communicate state changes in realtime to the frontend.
Domain events naturally fit this workflow by allowing these operations to be decoupled and processed asynchronously.

Additionally, using domain events enables loose coupling between components. For example, the discovery component
doesn't need to know about backup operations; it can simply emit events when new files are found, and the backup
component can subscribe to these events.

Designing event handlers to be idempotent enables the safe use of external queues
which often have at-least-once delivery guarantees.
When using external queues, ordering is typically guaranteed only 
within the context of a single aggregate root instance, not across aggregates. 
Additionally, ordering is very tricky in terms of event retries.
Therefore, event handlers must be designed to be able to handle out-of-order events.

Using domain events carries the risk that the state of the system may have changed by the time the event is processed.
Event handlers must be designed to handle this scenario gracefully.

When designing event handlers, there are two ways to implement them:
- A single handler per event,
- A separate handler per action.

Using a single handler per event is simpler and less verbose. However, it means that one failed action can 
prevent others from being completed. Separate handlers per action isolate the failure, making overall event handling
more robust.

## Decision

- Prefer domain events over direct calls when one or more of the following apply:
    - The interaction would otherwise require modifying multiple aggregates in a single transaction,
    - Inverting a dependency between components is desirable,
    - The emitting component has no meaningful reason to know about the reacting component.
- Name events in the past tense and suffix them with `Event` (e.g. `FileDiscoveredEvent`, `BackupCompletedEvent`).
- Events must carry only the minimal data needed to identify what occurred (thin events). Handlers that
  require additional context are responsible for querying it.
- Event side effects must not be part of the same transaction as the state change that produced them,
  to avoid modifying multiple aggregates within a single transaction. Make event listeners asynchronous to achieve this.
- Implement separate event listeners and handlers for each action.
  - Name event listeners and handlers using the `<Action>On<Event>Listener/Handler` naming convention.
- Design event handlers to be idempotent, to satisfy at-least-once guarantees.
- Design event handlers to re-validate preconditions against the current state before acting
  and treat a changed or missing entity as a valid terminal condition rather than an error.
- Assume that events are ordered only in the context of an aggregate root instance, except for failed events
  being reprocessed out-of-order.

## Consequences

### Positive

- Better separation of concerns between components due to reduced coupling.
- Easier to add new features by subscribing to existing events.
- Avoids cross-aggregate transactions, in keeping with DDD principles.

### Negative

- Increased complexity in tracking the flow of operations due to increased indirection.
- Potential for event handling race conditions.
- More code to maintain compared to direct calls.
- Ambiguity in event ordering when an event handler fails to process an event. 