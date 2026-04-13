---
title: Use the outbox pattern
status: accepted
scope: backend
tags: ['architecture']
---

## Context

### Event handling issues

Handling events in the same transaction as the publisher is how Spring events work out of the box.
It's convenient, as it requires no additional code and any actions triggered by the user can be all-or-nothing
(either everything succeeds or nothing succeeds).

However, not all actions are database operations, and not all of them can be rolled back in case the transaction fails.
For example, an e-mail notification may be sent after successfully backing up a file, but another event handler might
throw an exception, rolling back the backup operation.
If file deletion on rollback is not handled explicitly, this will leave an orphaned file on disk.
If it is, we'll be left with a file that has been deleted despite the e-mail being sent that it was backed up.
Even worse, the e-mail action itself might throw an unhandled exception,
leading to the file being deleted without a good reason.

There is also a risk of losing events before they're processed if the application crashes at the wrong time.
This would lead to event handlers never being triggered for some events, leading to data inconsistency.

While it's less problematic (as it can happen regardless), increasing the number of things done in a single transaction 
also further increases the risk of orphaned files due to the increased time between file creation and status update. 
Because those two operations don't happen atomically, there is a window of time when the file is created, 
but the status update is never committed. 
The more time between these two operations, the higher the risk of the application crashing.

Another issue with same-transaction event handlers is that they would violate the previous architectural decision
to avoid using transactions outside of repositories (ADR-0007).
This would cause confusion and uncertainty regarding transactional guarantees,
as well as increase the risk of optimistic locking exceptions
(as many aggregates would be modified in the same transaction).

Simply making events run in separate transactions would solve the increased risk of orphaned files and compatibility
with the previous ADR. However, it would also remove the benefits of the all-or-nothing single-transaction approach,
while the problem of losing events before they're processed would still be present.

Using the outbox pattern solves all the issues above, except it only reduces the problem of
orphaned files. Any time a database commit happens as a result of a non-database operation (such as changing aggregate
status after creating a file), the database commit cannot be guaranteed unless some compensation mechanism is used.

At the same time, it might not be necessary to use the outbox pattern for all events. 
Some events (such as those informing of file copy progress) might only be used for use cases such as WebSockets
notifications and may be safely discarded after the application crashes. In those cases, using asynchronous events
is enough.

### Spring Modulith

The easiest way to implement the outbox pattern is to use Spring Modulith's externalized events.
To adhere to Ports & Adapters principles, as well as to protect the domain from infrastructural concerns,
separate "outbox" versions of all domain events must be defined, as well as mappers to convert between them.
Otherwise, database infrastructure concerns would leak into the domain.

The biggest downside of using Spring Modulith's outbox is that it doesn't guarantee event ordering - failed events
are skipped and retried later, which can lead to events being processed in a different order than they were published.
However, a different approach would pose its own challenges, as one failed event would have to prevent others from
being processed (to ensure consistent ordering), potentially locking up the entire system.
This could be mitigated by splitting processing by aggregate root id,
but the amount of work required to implement it might not be worth the benefit.
Additionally, such splitting would not guarantee order of side effects applied from different events
(e.g., if events from two different aggregates X both modify aggregate Y, we would not be able to guarantee order
of modification for Y).

Because we cannot guarantee absolute order of events, but also due to lack of an exactly-once delivery guarantee,
each event handler should be idempotent. The easiest way is to skip handling the event if the application is in
a different state than is assumed by the event handler.

Another issue is that, by default, Spring Modulith saves full class names of event listeners in the database
and relies on them when retrying failed events, which makes it much harder to refactor event handlers.
However, this is easily solved by specifying a listener id
(e.g., `@TransactionalEventListener(id = "consistent-listener-name")`,
`@ApplicationModuleListener(id = "consistent-listener-name")`).

Finally, the outbox pattern makes it harder to evolve events, as they are persistent - in Modulith's case, 
they're stored as JSON. With no standardized way to edit JSON columns, one must either use Liquibase's `customChange`
or commit to only supporting a database like PostgreSQL (which supports JSON column editing).
Otherwise, event evolution will have to be very limited.
In the case of the earlier approach, additional care must be taken to ensure the queries remain database-agnostic.

## Decision

- Use Spring Modulith externalized events to implement the outbox pattern.
- Define separate "outbox" versions of all domain events which must not be discarded
  in case of application crash or restart.
- Use asynchronous event listeners for all domain events that may be discarded
  in case of application crash or restart.
- Assign a unique, stable id for each event listener to protect against issues related to class/method name changes.
- In rare cases where listener id must be changed for readability,
  always write a migration for the event_publication table's listener_id column.
- Use Liquibase's `customChange` to edit JSON columns containing outbox event bodies.

## Consequences

### Positive

- Domain events are handled after they're committed, guaranteeing that side effects are not premature.
- Important event handlers are guaranteed to be triggered,
  due to events being saved with the aggregate and republished after a crash.
- No unnecessary overhead for unimportant events, as they are not stored in the outbox table.
- Decreased risk of orphaned files, due to the short time between status update and commit.
- Compatibility with the previous ADR, due to not enforcing transactionality over several aggregates.

### Negative

- Increased complexity, due to having to write outbox event mappers.
- Increased latency for event handlers, due to the extra round-trip to the database.
- Order of events is no longer guaranteed, due to Spring Modulith skipping failed events and retrying them later.
- Potential issues with refactoring, due to the full class name of event listeners being saved in the database.
- Potential issues with evolving events, due to them being persisted as JSON.
