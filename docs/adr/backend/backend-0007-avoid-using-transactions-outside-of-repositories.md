---
title: 0007 - Avoid using transactions outside of repositories
parent: Architecture Decision Records (backend)
---

## Status
Accepted
{: .label .label-green}

## Context

...

Reasons against using transactions outside of repositories:

- Changing multiple aggregates in one transaction creates the requirement that they are stored in the same database, 
which limits our future architectural choices.
- Changing multiple aggregates in one transaction increases risk of resource contention
(the records are "locked" in the database while we work on them, or we get optimistic locking exceptions).
- throughput?
- Not everything can be rolled back - e.g., backed up files.
- Slow non-db operations (e.g., a slow API call) within a transactional context
  can keep a connection open for a long time. 
  
// @TODO Reverse this decision, mention common dev consensus that application layer should manage transactions,
//       this approach being more aligned with using Monolith Events without outbox?
//       BUT! This means Spring will invade the application layer (`@Transactional`)
//       OR perhaps this can be done with Spring AOP (e.g., for every class annotated with `@UseCase`)
//       BUT what about already downloaded files when a db rollback happens? I don't think we can handle that gracefully. 

## Decision

- ...

## Consequences

### Positive

- ...

### Negative

- ...
