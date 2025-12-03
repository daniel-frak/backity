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

## Decision

- ...

## Consequences

### Positive

- ...

### Negative

- ...
