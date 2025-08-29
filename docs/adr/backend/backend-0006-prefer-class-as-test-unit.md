---
title: 0006 - Prefer to treat a single class as a unit for testing
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

In the context of unit testing, a unit can either be a single class or a collection of classes.
In the first approach, each class is tested in isolation.
Dependencies are usually replaced by test doubles, though real value objects are often used. 
In the second approach, there is a "main" class that is the System-Under-Test,
with others being tested implicitly as dependencies.
The main difference is that those dependencies don't have tests of their own.

When treating a single class as a unit, it's easier to think about edge cases for it,
as that class is the focus of each test.  
Additionally, it's easier to write tests for these use cases,
as the class can be directly manipulated into the desired state.
Some developers note that having many small tests like that makes large refactorings more time-consuming,
as any architecture change will require changes in the tests.
The defense given for this is that any units made of a collection of classes are
usually too small to make a noticeable difference,
and that refactorings big enough to touch multiple classes probably warrant new test-driven documentation anyway.

An often cited problem with treating a single class as a unit is classes needing extensive collaborator mocking,
which makes the tests harder to read and write, compared to the alternative approach. However, this
should be treated as a code smell and such classes should be refactored.

According to proponents of the opposite approach, treating several classes as a unit
is more robust, as changes to the underlying classes will usually not require changes in the tests.
However, this approach reduces the focus on the implicitly tested classes,
which means that their edge cases are less likely to be tested.
When the developers do try to test them, it's often more challenging due to having to do it in a larger context,
and sometimes impossible due to the main class not allowing the dependencies to enter a necessary state.

Additionally, some of the implicitly tested classes may be reused in other places, which makes it unclear
which functionality should be tested as part of which unit. This may lead to classes being either under-tested
or over-tested and makes tests less useful as documentation.

Finally, treating several classes as a unit makes it more challenging to identify missing tests,
as a lack of a corresponding test class does not necessarily mean that the class is untested.

## Decision

- Prefer treating a single class as a unit (i.e., a test class per class approach).
- Prefer using real value objects in all unit tests

## Consequences

### Positive

- Higher likelihood of edge cases being tested, due to focus placed on a single class.
- Easier to test edge cases, as the class can be directly manipulated into the desired state.
- Easy identification of missing tests, as each class has its own test class or is likely untested.
- Tests-as-documentation are easy to locate, as they always live next to each class.
- No ambiguity about which functionality should be tested as part of which unit, due to reusable classes
  having their own test classes.

### Negative

- Larger refactorings may require writing or rewriting a larger number of tests.