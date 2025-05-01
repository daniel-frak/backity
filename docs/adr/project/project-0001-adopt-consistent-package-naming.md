---
title: 0001 - Adopt Consistent Package Naming Conventions
parent: Architecture Decision Records (project-wide)
---

## Status

Accepted
{: .label .label-green}

## Context

Without a clear and consistent package naming standard, developers may use inconsistent naming styles,
leading to confusion about package purposes and contents.

## Decision

- Use **singular names** for packages that represent a single entity, concept, or abstraction
  (e.g., `strategy`, `payee`, `validation`).
- Use **plural names** for packages that contain multiple implementations or variations of a concept
  (e.g., `origins`, `bases`, `rates`).

## Consequences

### Positive
- Developers can quickly understand package purposes, improving team communication and collaboration.
- Predictable conventions make it easier to navigate the codebase.

### Negative

- Strict adherence to naming rules may occasionally feel restrictive, especially for edge cases.
