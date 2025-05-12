---
title: 0005 - Use Pitest for mutation testing
parent: Architecture Decision Records (backend)
---

## Status

Accepted
{: .label .label-green}

## Context

Traditional coverage metrics don't guarantee that all important code paths have been tested,
reducing confidence in automated tests.
Mutation testing uncovers these gaps by introducing small code changes (“mutants”) and verifying that tests fail
when behavior changes.

Pitest is a mature, Java-focused mutation testing tool with a dedicated Maven plugin, making it easy to use as part
of a CI pipeline. It integrates with SonarQube (though not with SonarCloud) via an unofficial `mutationanalysis` plugin,
and is currently the only viable choice for mutation testing in Java projects.

Mutation testing can significantly extend build times,
especially when applied to integration tests that spin up application contexts or external dependencies. 
Using more mutators improves defect detection but further increases CI feedback time.

Mutation analysis may report false positives (e.g., equivalent mutants), 
while certain code paths (such as complex logging logic or configuration wiring) are impractical to test to the
satisfaction of the tool.

## Decision

- Introduce mutation testing into the CI pipeline using Pitest:
  - Use every mutator available.
  - Fail the build if any mutants survive.
  - Ignore integration tests during mutation testing
- Configure local SonarQube instance to show surviving mutants through the `mutationanalysis` plugin.
- Limit mutation analysis to unit tests.
- Manually exclude classes covered only by integration tests (such as controllers, repositories, queue listeners, etc.)
  by adding wildcard patterns under `<excludedClasses>` in the Pitest Maven plugin configuration.
- Annotate methods that contain false positives or are impractical to test with a custom `@DoNotMutate` annotation
  to suppress mutation analysis.

## Consequences

### Positive

- Strengthens test suites by verifying that there are no unexpected gaps in tests.
- Prevents new code from degrading true test coverage through CI failures on surviving mutants.

### Negative

- Requires much higher code coverage than many developers are used to, which may lead to apprehension.
- Higher requirements for code may lead to fewer contributions.
- Increases CI duration (currently by about 30%, from ~4 to ~6 minutes).
- Does not provide increased confidence for code that is excluded (such as classes which are only integration tested).
- Mandates manual updates to `<excludedClasses>` for new integration-only classes to avoid unexpected build failures.
- May report false positives, requiring use of `@DoNotMutate`.
- Adds configuration complexity due to maintenance of POM exclusions and annotation usage.
