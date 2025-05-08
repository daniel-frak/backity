---
title: 0001 - Prefer ArchUnit as an Architectural Decision Record store
parent: Architecture Decision Records (backend)
---

## Status
Accepted
{: .label .label-green}

## Context

Written ADRs are not as effective as executable ones, as they can be missed or ignored by developers.

An ADR which verifies itself against the codebase automatically enforces architectural rules. 

However, tools like ArchUnit aren't inherently ADR tools, so architectural decisions written with them 
might lack useful information such as context and consequences, unless a consistent format is used.
This could lead to the rule being eventually removed due to being poorly understood.

## Decision

- Write ADRs as ArchUnit rules instead of a Markdown files whenever possible.
- When writing an ArchUnit rule, always include a reason (`because(...)`), to explain why the rule is in place.
- When deprecating or superseding rules, mark them as `@Disabled`, write additional information such as the superseding
  rule using Javadoc.
- When writing an ArchUnit rule is not possible or practical, write down the rule using Markdown.
- The ArchUnit rule should follow the format (minus the comments):
  ```
  // Status - accepted, because not @Disabled
  @ArchTest
  static final ArchRule RULE_NAME = // Title
          [ArchUnit rule code] // Decision
          .because("""
                  [A single-sentence reason for the rule, a suffix to '[...], because [...]'].
                  
                  Context:
                  [Describes the forces at play, including technological, political, social, and project local. 
                  These forces are probably in tension, and should be called out as such.
                  The language in this section is value-neutral. It is simply describing facts.]
                  
                  Positive consequences:
                  [Bullet points]
                  
                  Neutral consequences:
                  [Bullet points, optional]
                  
                  Negative consequences:
                  [Bullet points]
                  """);
  ```

## Consequences

### Positive

- At least some architectural design decisions will be automatically enforced, making it easier to keep the project
  in a consistent style

### Negative

- Architectural design decisions will be spread across multiple places, making it harder to keep track of
- Some ArchUnit rules may be challenging to write
- Adds a dependency on ArchUnit
