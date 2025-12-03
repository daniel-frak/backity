---
title: 0008 - Decouple event listeners from handlers
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

The process of discovering and backing up game files involves multiple asynchronous operations. Game files need to be
discovered from different providers, validated, and backed up - all of which can happen independently.
Moreover, the backend must communicate state changes in real-time to the frontend, via WebSocket messages.
Domain events naturally fit this workflow by allowing these operations to be decoupled and processed asynchronously.

Additionally, using domain events enables loose coupling between components. For example, the discovery component
doesn't need to know about backup operations - it simply emits events when new files are found, and the backup
component can subscribe to these events.

// @TODO Write about assumed ordering guarantees, need for idempotency, asynchrony, and error handling.

// @TODO Write arguments against domain events.

## Decision

- Use domain events for communication between major components.
- Implement event handlers as separate classes that subscribe to specific events.
- Design event handlers to be idempotent.
- Assume that events are ordered only in the context of an aggregate root instance.

## Consequences

### Positive

- Better separation of concerns between components
- Easier to add new features by subscribing to existing events
- Improved scalability through asynchronous processing // @TODO Write about how this isn't needed for Backity?
- More resilient system as components can fail independently // @TODO Delete this?

### Negative

- Increased complexity in tracking the flow of operations
- Potential for event handling race conditions
- Need for careful error handling in asynchronous operations