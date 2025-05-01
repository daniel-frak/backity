---
title: 0002 - Use Conventional Commits
parent: Architecture Decision Records (project-wide)
---

## Status

Accepted
{: .label .label-green}

## Context

Commit messages are a key artifact in the development workflow, used for purposes such as tracking changes,
generating release notes, and debugging issues. 
A well-structured commit message provides context for code changes and simplifies various development tasks.
Adopting a standardized format can provide consistency and simplify tooling integrations.

## Decision

The project will adopt the [Conventional Commits](https://www.conventionalcommits.org/) specification
for commit messages. Contributors will use the format `type(scope): description`, where:

- `type` specifies the type of change (e.g., `feat`, `fix`, `refactor`, `docs`, `ci`).
- `scope` (optional) indicates the part of the project affected.
- `description` provides a concise summary of the change.

An example commit message following this specification may look like one of these:

- `feat: Allow stopping file discovery`
- `feat(frontend): Allow stopping file discovery`
- `feat(backend): Allow stopping file discovery`
- `test: Add missing tests for S3 repository`
- `fix: Fix application crashing during startup`
- ```
  feat!: Update GOG API to v2.0
  
  BREAKING CHANGE: Old GOG API no longer supported
  ```

## Consequences

### Positive

- Clear and uniform commit messages enhance readability of the project history.
- Encourages atomic commits focused on a single class of change, 
  improving clarity and reducing the risk of introducing bugs.
- Simplifies automation processes like changelog generation and release management.

### Negative

- Contributors will need time to adapt to the format and learn its conventions.
- Manual enforcement of the format may result in occasional inconsistencies in commit history.
