---
title: 0001 - Prefer ArchUnit as an Architectural Decision Record store
parent: Architecture Decision Records (backend)
---

## Status
Accepted
{: .label .label-green}

## Context

Written ADRs are not as effective as executable ones, as they can be missed or ignored by developers.

An ADR which verifies itself against the codebase is preferable.

## Decision

- Write an ArchUnit rule instead of a Markdown file to write an ADR whenever possible
- When writing an ArchUnit rule, always include a reason (`because()`), to explain why the rule is in place
- When writing an ArchUnit rule is not possible or practical, write down the rule using Markdown

## Consequences

### Positive

- At least some architectural design decisions will be automatically enforced, making it easier to keep the project
  in a consistent style

### Negative

- Architectural design decisions will be spread across multiple places, making it harder to keep track of
- Some ArchUnit rules may be challenging to write
- Adds a dependency on ArchUnit
