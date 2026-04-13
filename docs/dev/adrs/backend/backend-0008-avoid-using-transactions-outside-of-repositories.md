---
title: Avoid using transactions outside of repositories
status: accepted
scope: backend
tags: ['architecture']
---

## Context

Historically, transactions have often been started in application services or higher layers,
encompassing multiple repository calls and non-database side effects. This leads to transactional
scopes that extend beyond a single aggregate.

### Reasons for using transactions outside of repositories

- Familiarity: many frameworks encourage transaction management at the service layer.
- Simpler implementation of use cases through immediate consistency (atomic modification of multiple aggregates).
- Invariants can be easily enforced across aggregates.
- An all-or-nothing consistency model is easier to reason about
  (though only if there are no non-database side effects).
- Reduced need for messaging infrastructure (events not necessary for ensuring consistency).

### Reasons against using transactions outside of repositories

- Not all side effects are transactional.
  File storage, message publishing, third-party API calls, or email sending
  cannot be atomically rolled back with the database, creating a false sense of atomicity.
  For example:
  ```java
  @Transactional
  public void processBackup(Backup backup) {
    fileStorage.upload(backup.getId(), backupData);  // Not rolled back if transaction fails.
  
    backup.markAsCompleted();
    notificationService.send(backup.getUserId());     // Not rolled back if transaction fails.
  
    // By this point the notification is already sent.
    backupRepository.save(backup); // If this throws, the file is orphaned.
  
    // Additional danger - anything that fails at this point will roll back the Backup aggregate being saved.
    emailService.sendBackupCompletionEmail(backup.getUserId(), backup.getId());
  }
  ```

- Changing multiple aggregates in one transaction requires them to share the same database,
  limiting future architectural choices (e.g., splitting into separate services,
  or moving one aggregate to a different storage technology).
 
- Updating multiple aggregates in one transaction increases the chance that
  concurrent requests will block or conflict with each other.
  In the case of optimistic locking, it may lead to higher retry rates.

- If such transactions also include additional work (e.g., extra queries or
  external calls), they tend to remain open longer, which reduces throughput
  by holding connections and locks for extended periods.

- Slow non-database operations (e.g., remote API calls, file processing) within a transaction
  keep connections and locks open longer, increasing contention and risking connection pool exhaustion.

- Multi-aggregate transactions disincentivize proper aggregate root design 
  by allowing invariants to be enforced across aggregates.

- Handling transactions outside of repositories leaks persistence concerns outside the specific adapters.
  Using the unit of work pattern assumes that the underlying persistence technology supports transactions,
  which may not be true, e.g., for in-memory or file-based repository implementations.
  As mentioned earlier, the unit of work pattern also suggests that all actions within it can be rolled back
  automatically, which is not the case for side effects which are not part of the specific database transaction.

Using a transaction outside a repository does not automatically involve multiple aggregates or non-database operations. 
However, enforcing this consistently across the codebase is challenging, if not impossible.

### Project-specific context

This project is unlikely to grow to the point where splitting into separate services
or moving one aggregate to a different storage technology than others would be necessary.
Slow database operations are also unlikely to be a problem. Concurrency issues are likely to be rare, as well.

Given all this, even poorly designed aggregates may not cause immediate operational problems, 
though multi-aggregate transactions can encourage aggregates that don’t fully enforce their own invariants,
making the domain harder to reason about.

However, as not all side effects are transactional, explicitly modeling this through avoiding 
multi-aggregate transactions will bring clarity and reduce the risk of bugs 
(e.g., forgetting to delete a file if a transaction rolls back).
Adopting this approach will incentivize proper aggregate root design, 
making it easier to reason about invariants.

Avoiding cross-aggregate transactions naturally leads to using eventual consistency for coordination between aggregates.
This project is naturally suited to this, as its primary function (backing up files)
is inherently asynchronous and eventually consistent.

This means the complexity cost of eventual consistency is lower than in domains
requiring strong synchronous consistency guarantees.

Finally, this decision can be reversed more easily than allowing broadly scoped transactions  
because keeping transactions small now prevents different parts of the system from becoming too tightly coupled.

## Decision

- Avoid using transactions outside of repository operations.
- Avoid cross-aggregate transactions.
- Avoid transactions that include non-database side effects.
- Prefer using eventual consistency when cross-aggregate coordination is required.
- Transactions outside repositories require explicit justification in code comments or ADRs,
  documenting why they are necessary.
- Cross-aggregate transactions require explicit justification in code comments or ADRs,
  documenting why eventual consistency is inappropriate for that specific case.

## Consequences

### Positive

- Clear separation of concerns due to persistence logic remaining fully inside repositories.
- Clear consistency boundaries due to transactions aligning with aggregate boundaries.
- Reduced cognitive load due to not having to wonder if there are non-database side effects in a transaction.
- Lower risk of bugs due to no need to roll back non-database side effects after a transaction fails.

### Neutral

These are benefits that are unlikely to be significant in this project:

- Reduced database contention due to shorter-lived locks and transactions.
- Lower risk of connection pool exhaustion due to avoiding long-running transactions.
- Improved scalability due to aggregates not being forced into the same database.
- Higher throughput due to smaller transactional scopes.

### Negative

- Increased implementation complexity due to eventual consistency mechanisms.
- Potential temporary cross-aggregate inconsistencies due to lack of atomic multi-aggregate transactions
  (though backup operations are inherently asynchronous, so users already expect this).
- Higher risk of bugs from new developers due to them treating use cases as atomic
  (though this might be mitigated by good aggregate design).
