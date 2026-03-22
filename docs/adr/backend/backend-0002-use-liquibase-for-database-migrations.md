---
title: 0002 - Use consistent naming conventions for Liquibase migrations
parent: Accepted
grand_parent: Architecture Decision Records (backend)
---

## Context

The project needs a reliable way to manage database schema changes.
Liquibase is a popular tool for this due to its ability to handle schema migrations with rollback support.
However, a changelog organization style must be chosen for the project.
Additionally, a lack of standardization in naming scripts and changesets can cause confusion
and errors during migration.

### Changelog file format

Liquibase supports several file formats for changelogs:
- XML
- SQL
- YAML
- JSON

Out of these, XML and YAML are the most common. Out of the two, YAML is preferred due to its simplicity.

### Changelog organization styles

Liquibase proposes three organization styles:

1. Organizing by object (e.g., tables/views/indexes, or per table)

a) Separate changelog per database type (tables, views, indexes)

This approach splits changes by technical concern, instead of by logical unit,
which makes it difficult to follow the change history.

b) Separate changelog per table

This approach makes it challenging to implement changes that rely on changes in other tables.

2. Organizing by release version

Liquibase sorts files alphabetically, so `0.10.0.yml` would come before `0.2.0.yml`, making this approach unreliable.

3. Using `includeAll` with a timestamped prefix for changelog files

This approach greatly simplifies migration management, as new changelogs are automatically detected.
However, in a multi-developer workflow it cannot guarantee logical execution order because PR may be merged out of order
(leading to a script with a later timestamp being applied before a script with an earlier timestamp).

As each of the approaches above has distinct disadvantages, forgoing complex organization and 
explicitly ordering changelogs within the root changelog file seems like the best option.
The primary benefit of this approach is that, if two PRs modify the database,
a merge conflict for the root changelog will force the developer to explicitly specify the desired order.

It's common to add a prefix to each changelog file.
However, in this scenario, adding a date prefix to the changelog file could again lead to ordering issues
if a PR is merged which modifies a changelog file from a different day than the last applied one.
Using number prefixes (e.g., `001-`) could lead to duplicate prefixes, in case two PRs get created at the same time.
Therefore, it's better to avoid prefixes altogether.

A suffix may be used to avoid naming conflicts if two changelogs do essentially the same thing at different times.
However, the suffix should not be relied on for ordering.

## Decision
 
- Use Liquibase for database migrations
- Use YAML for changelog files
- Name changelogs as `{changeTitle}.yml`
- Add a `YYYY_MM_DD` suffix to the changelog in case of naming conflicts, 
  using the current date at time of writing the code.
- Manually order changelogs within the root changelog file.
- Ensure all migrations are **backwards compatible** to allow safe rollbacks and non-breaking deployments.

## Consequences

### Positive

- Standardized changelog names improve clarity and ease of use.
- Using changelog suffixes avoids naming conflicts.
- Manual ordering of changelogs prevents changelogs from being applied out of order.
- Backwards compatibility reduces deployment risks and supports rollback processes.

### Negative

- Additional effort is required to maintain the root changelog.
- Additional effort may be required to ensure every migration is backwards compatible.
