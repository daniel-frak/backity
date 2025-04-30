# Use consistent naming conventions for Liquibase migrations

## Status

Accepted

## Context

The project needs a reliable way to manage database schema changes.
Liquibase is the chosen tool due to its ability to handle schema migrations with rollback support.
However, a lack of standardization in naming scripts and changesets can cause confusion and errors during migration.

## Decision
 
- Name migration scripts as `db.changelog-{currentTimestamp}_{scriptName}.xml`.
- Name changesets within scripts using the pattern `{orderNumber}_{changeSetName}`.
- Ensure all migrations are **backwards compatible** to allow safe rollbacks and non-breaking deployments.

## Consequences

### Positive

- Standardized script and changeset names improve clarity and ease of use.
- Backwards compatibility reduces deployment risks and supports rollback processes.

### Negative

- Additional effort may be required to ensure every migration is backwards compatible.
