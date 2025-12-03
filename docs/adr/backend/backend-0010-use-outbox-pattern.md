---
title: 0009 - Use the outbox pattern
parent: Architecture Decision Records (backend)
---

## Status
Accepted
{: .label .label-green}

## Context

...

Handling events in the same transaction as the publisher is how Spring events work out of the box.
It's convenient, as it requires no additional code and any actions triggered by the user can be all-or-nothing
(either everything succeeds or nothing succeeds).

However, not all actions are database operations, and not all of them can be rolled back.
For example, an e-mail notification may be sent after successfully backing up a file, but another event handler might
throw an exception, rolling back the backup operation and deleting the file despite the e-mail being sent.
Even worse, the e-mail action itself might throw an unhandled exception,
leading to the file being deleted without a good reason.

There is also a risk of losing events before they're processed if the application crashes at the wrong time.
This would lead to event handlers never being triggered for some events, leading to data inconsistency.

Additionally, with events handled in the same transaction as the publisher,
we increase the risk of orphaned files due to the increased time between status update and commit.
E.g., after a file is properly backed up and its status updated in the database,
the application might crash while handling an event triggered by that action, thus never committing the status update.

Another issue with same-transaction event handlers is that they would violate the previous architectural decision
to avoid using transactions outside of repositories (ADR-0007).
This would force all aggregate roots to be stored in the same database
and increase the risk of optimistic locking exceptions.

Simply making events run in separate transactions would solve the increased risk of orphaned files and compatibility
with the previous ADR. However, it would also remove the benefits of the all-or-nothing approach,
while the problem of losing events before they're processed would still be present.

The easiest way to implement the outbox pattern is to use Spring Modulith's externalized events.
To adhere to Ports & Adapters principles, as well as to protect the domain from infrastructural concerns,
separate "outbox" versions of all domain events must be defined, as well as mappers to convert between them.

The biggest downside of using Spring Modulith's outbox is that it doesn't guarantee event ordering - failed events
are skipped and retried later, which can lead to events being processed in a different order than they were published.
However, a different approach would pose its own challenges, as one failed event would have to prevent others from
being processed, potentially locking up the entire system. This could be mitigated by splitting processing by
aggregate root id, but the amount of work required to implement it might not be worth the benefit.

Another issue is that the default Modulith implementation saves full class names of event listeners in the database
and relies on them when retrying failed events, which makes it much harder to refactor event handlers.

Finally, the outbox pattern makes it harder to evolve events, as they are persistent - in Modulith's case, 
they're stored as JSON. With no standardized way to edit JSON columns, one must either use Liquibase's `customChange`
or commit to only supporting a database like PostgreSQL. Otherwise, event evolution will have to be very limited.
In the case of the earlier approach, care must be taken to ensure the queries remain database-agnostic.

// @TODO Reverse this decision, because reporting event handling can be done `@Async` and low risk when it fails?
// @TODO Consider simpler user maintenance when each user action either succeeds or fails 'atomically'.
// @TODO Bulk actions better with outbox pattern because can create thousands of events AND potentially process them in parallel?
// @TODO Should `spring.modulith.events.externalization.serialize-externalization` be `true`?
// @TODO Consider a "defer decision" decision - don't use outbox but write code (e.g., event handlers) as if we were,
//       Decide whether to use outbox later.

## Decision

- Use Spring Modulith externalized events to implement the outbox pattern.
- Define separate "outbox" versions of all domain events.
- Use Liquibase's `customChange` to edit JSON columns.

## Consequences

### Positive

- Event handlers are guaranteed to be triggered,
  due to events being saved with the aggregate and republished after a crash. 
- Decreased risk of orphaned files, due to the short time between status update and commit.
- Compatibility with the previous ADR, due to not enforcing transactionality over several aggregates.

### Negative

- Increased complexity, due to having to write event mappers.
- Increased latency, due to the extra round-trip to the database.
- Order of events is no longer guaranteed, due to Spring Modulith skipping failed events and retrying them later.
- Potential issues with refactoring, due to the full class name of event listeners being saved in the database.
- Potential issues with evolving events, due to them being persisted as JSON.
