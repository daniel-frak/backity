---
title: Adhere to the test pyramid
status: accepted
scope: project
tags: ['testing']
---

## Context

Many projects gravitate towards a testing strategy consisting primarily of E2E and integration tests, 
the so-called Test Ice Cream Cone.
These tests exercise real system behavior and require less knowledge of the codebase internals.

However, this leads to tests which are:
- slow (due to having to exercise the entire application),
- complicated to set up (due to having little control over the SUT's dependencies and limited access to its API),
- hard to understand (due to complex test code and hard-to-define SUT),
- very brittle (any change in the system might break them, and depending on shared state makes flakiness more likely).

Additionally, this exclusively outside-in view of testing makes it hard to focus on possible edge cases,
and even harder to properly test them. In cases of defensive programming, it might often be impossible to bring
the entire system to a state we want to test.

Such an approach eventually leads to a point where refactoring becomes risky, as:

- the expected behavior of each class is not properly documented as unit tests
  (making it harder to understand the expected behavior),
- it is not immediately obvious where each class is tested (or if it's tested at all), with tests being
  scattered across several E2E/integration test classes (making us less confident in our refactoring),
- tests for specific classes are likely to be duplicated across several E2E/integration tests
  (increasing the work needed to update tests),
- even a small change will require running the slow suite of E2E/integration tests (causing a slower feedback loop).

Risky refactoring and the bugs that might come up due to the lack of a robust testing strategy may eventually lead to
increased delivery times for new features.

On the other hand, adhering to the test pyramid makes it so that we write more unit tests, which:

- are much faster and can be run in parallel (making the feedback loop significantly faster),
- fail due to much more localized issues (making it much easier to diagnose the problem and making them less brittle)
- do not rely on shared state (making them less likely to be flaky)
- have fewer moving parts and give us more control over them (making them much easier to set up)
- force better modularity and separation of concerns (leading to cleaner architecture)

At integration points, where unit tests are not practical, focused integration slice tests can provide the verification
needed to validate edge cases in isolation from irrelevant parts of the system.

Writing a small number of happy-path E2E tests can help verify that entire user journeys are working correctly,
catching complex workflow issues (e.g., an item failing to delete because of a dependency on something else).  

## Decision

- Write unit tests whenever possible.
    - Do not share any state between unit tests.
- Write integration tests at integration points (e.g., controllers, repositories, listeners, publishers).
    - Write them as slice tests (don't initialize more of the application context than you need)
        - Mock those parts of the system which are irrelevant to the test
          (e.g., we don't need real use cases when testing a controller response)
    - For controller tests, make sure to test the API contract
      (e.g., assert on the full JSON response, HTTP code, etc.).
    - For repository tests, make sure to test all edge cases. Assume the repository works correctly in all other tests.
- Write a tiny amount of happy-path E2E for the most critical functionality.
- Exclude E2E tests from code coverage metrics.

## Consequences

### Positive

- Fast feedback loop due to focusing on lightweight unit tests which can run in parallel.
- Improved reliability due to smaller, more focused tests being less flaky and easier to debug.
- Less test duplication and overlap due to tests living close to their tested code.
- Easier testing of edge cases due to easier access to SUT API and more control over dependencies.
- Safer refactoring due to fast tests acting as easy-to-find documentation.
- Increased long-term speed of delivery due to all of the above.

### Negative

- Increased upfront effort due to writing and maintaining more unit and slice tests
- Contributors accustomed to writing E2E tests may find it hard to adjust to writing unit or integration tests.