---
title: Use multiple Static Code Analysis tools
date: 2026-10-25
status: accepted
scope: project
tags: ['tooling']
---

## Context

Static code analysis helps detect issues early and maintain consistent coding standards across the project.
In an age of (often undisclosed) LLM code extrusion, automated code quality verification is more important than ever.

SonarQube is a popular static analysis tool, supporting a wide array of rules,
as well as providing a dashboard that summarizes code-quality findings across the codebase - 
including test coverage, code smells, and bugs.
It supports many programming languages, making it ideal for a full-stack project like this one.
Its cloud version, SonarCloud, can be used to provide a handy overview of each Pull Request's code quality.
However, its free version doesn't natively support external forks,
requiring workarounds like the `sonar-fork-analysis` GitHub Action to work.
These workarounds may be especially vulnerable to security issues.
SonarCloud is also an externally hosted dependency, which is not guaranteed to be freely available indefinitely.
While dockerized SonarQube could potentially be made to provide similar functionality
through some clever CI configuration and SonarQube community plugins
(such as mc1arke's `sonarqube-community-branch-plugin`),
that would require additional work (and maintenance).
Crucially, during local development, a Dockerized version of SonarQube must be run for the analysis to work,
while the analysis itself takes several minutes (excluding SonarQube's own startup time).
The list of violations is also only available through the dashboard.
This friction means many external contributors may (knowingly or not) skip that step,
only being informed of issues with their code when it's already part of a Pull Request.
SonarQube is also best suited for detecting more complex code smells, bugs and vulnerabilities,
rather than code style violations.

Checkstyle, on the other hand, offers plenty of code-style checks (though some more advanced checks are also available).
Its analysis takes only a few seconds, all violations are reported directly through the console, and it doesn't require
any dependencies running in the background. Therefore, it can be integrated as a default directly into the build system,
giving nearly immediate feedback during development.
However, it only supports Java and does not provide the same breadth of bug, vulnerability, and maintainability analysis
as SonarQube.

SpotBugs focuses on detecting bugs in Java code and, like Checkstyle, can easily be integrated into the build system
without requiring any dependencies running in the background.
However, its analysis takes tens of seconds, so it's more suited to a later Maven phase like `verify`.

These tools complement each other, having unique strengths in different areas.
In areas where they intersect, each of them has slightly different rules,
which maximizes opportunities for detecting code quality issues.

When equivalent rules overlap, the one running earliest in the development cycle should be the strictest,
so that violations are caught as early as possible.

ErrorProne was briefly considered, but testing revealed it doesn't seem fully compatible with Lombok, which this project
uses extensively.

Research also surfaced PMD as a viable SCA tool, but it didn't seem to have any unique strengths compared to the ones
above. This does not exclude the possibility of using PMD in the future.

## Decision

- Provide SonarQube for local code analysis, so that full code analysis is always available locally.
- Run SonarQube Cloud analysis on pull requests and require code to pass the quality gate before merging.
- Run Checkstyle in Maven's `validate` phase to automatically fail all builds on any violation.
- Run SpotBugs in Maven's `verify` phase to detect bugs early in the development process.
- Actively fix any inconsistencies where a Checkstyle check is less strict than its Sonar counterpart.

## Consequences

### Positive

- More immediate feedback for developers, due to code quality violations causing the build to fail locally.
- Less work for maintainers during code review, due to higher quality code in Pull Requests.
- Lower chance of bugs due to a consistent code style being enforced.

### Negative

- Additional tools to maintain (e.g., keeping versions up to date).
- Potentially fewer people willing to contribute due to mandating higher quality standards. 