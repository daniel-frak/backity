---
title: 0004 - Adopt Test Data Builder and Object Mother Patterns for Test Data Creation
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

Object creation in tests can become complex and repetitive, leading to duplication and fragility.
The [Test Data Builder](https://wiki.c2.com/?TestDataBuilder) and [Object Mother](https://wiki.c2.com/?ObjectMother)
patterns address these issues.

In terms of creating the domain objects themselves, there are two possible approaches:
- Make sure it's possible to build every object using its constructor and use it to construct each test object
  from scratch, no matter its state.
- Only build test objects in the same way that the application does it,
  starting with an object in a "newly created" state, and mutating it to the desired state by calling its methods.

Proponents of the latter approach argue that it guarantees that the domain objects are in a valid state.
However, this approach may lead to situations where an object is impossible to create, e.g.,:
- when certain fields, such as `version` are only ever modified in the database,
- when we actually want to create an object in an invalid state, for defensive testing purposes.
- when we want to create an object in a state that will be possible to create in the future, but is not yet implemented
  (e.g., when testing mapping from domain to DTO in an API-first approach).

Additionally, constructors can be made to enforce object invariants during their creation, preventing invalid states
when necessary.

## Decision

- For each object class used in a test, create an Object Mother class named like that object, with the prefix `Test`
  (e.g. `TestBackupTarget`).
  This class should encapsulate the details of how to create that object in every state necessary for tests.
    - The Object Mother should be in the same package as the object.
- Only construct test objects through an Object Mother class, e.g. `TestSomeObject.inStateX()`
    - Factory method names on the Object Mother must only describe the object's state (e.g., don't create method names
      including the object name like `TestSomeObject.someObjectInStateX()`).
    - When an object's state is complex, focus on describing the state relevant to the tests for which you need it.
    - You may create aliases for existing factory methods if doing so would make a test more readable.
      However, try to reuse existing methods as much as possible, as they are more likely to be familiar to the team.
- Allow test objects to be customized using the Builder pattern,
  by providing an Object Mother method suffixed with `Builder` for every factory method within in
  (e.g. `TestSomeObject.inStateXBuilder()`).
  - Strongly prefer hiding object construction details in Object Mother factory methods. Only use the builders
    when the test robustness would otherwise suffer.
  - Avoid using the `@Builder` annotation on domain objects.
- Do not use static imports for Object Mothers, as it would reduce the readability of code.
- Create test objects in their target state using their constructors instead of mutating them by calling their methods.

## Consequences

### Positive

- Tests will be more readable and easier to maintain due to object creation being abstracted away
  and unified across the suite.
- Tests will not have to be changed whenever an object's creation API changes due to that API being abstracted away.
- It will be easier to understand the role of complex objects due to their Object Mothers documenting their key states
  and the ways they are achieved.
- Code duplication will be reduced due to Object Mother methods being reused between tests.
- Every kind of test object can be easily created, due to object constructors allowing for all possible states.

### Negative

- Requires additional effort to maintain Object Mothers.