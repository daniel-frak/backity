---
title: 0004 - Adopt Test Data Builder and Object Mother Patterns for Test Data Creation
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

Object creation in tests can become complex and repetitive, leading to duplication and fragility.
The Test Data Builder and Object Mother patterns address these issues.

## Decision

- Use the [Test Data Builder](https://wiki.c2.com/?TestDataBuilder) pattern 
  with the [Object Mother](https://wiki.c2.com/?ObjectMother) pattern.
- Provide pre-configured builders via static methods, which can be adjusted for specific test cases.
- Avoid using the `@Builder` annotation on domain objects to prevent invalid states in production code.

## Consequences

### Positive
- Improves test readability by abstracting object creation details.
- Reduces the scope and effort of test refactoring when domain object constructors change.
- Prevents misuse of domain builders in production, ensuring domain integrity.

### Negative

- Requires additional effort to maintain builder and Object Mother implementations.