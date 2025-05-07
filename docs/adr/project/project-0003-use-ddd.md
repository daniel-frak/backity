---
title: Use Domain-Driven Design
parent: Architecture Decision Records (project-wide)
---

## Status

Accepted
{: .label .label-green}

## Context

Domain-Driven Design provides a structured approach to modeling complex domains through key concepts
like bounded contexts, ubiquitous language, and aggregates. 
These components help to isolate different parts of the domain,
ensuring that the **software reflects real-world processes with clarity and consistency**.

It traditionally relies on collaboration between developers, domain experts, and business stakeholders
to explore and refine the domain.
However, this project **lacks business stakeholders or formal domain experts**, and, 
like most open-source projects, relies on contributors and community discussions to develop domain understanding.

This creates a challenge: 
While DDD could provide valuable structure for domain modeling,
**we cannot follow the typical discovery processes found in traditional DDD implementations**.

Furthermore, contributors often **come from diverse backgrounds and may lack a unified understanding of the domain**.
This makes it difficult to maintain a consistent ubiquitous language or to define bounded contexts collaboratively. 
Moreover, **without a central authority like a business domain expert**, 
the process of refining and validating the domain model becomes more difficult.

Finally, **contributors may be unfamiliar with DDD**. This may be somewhat alleviated by directing them to resources
like ["Introduction to DDD" by InfoQ](https://www.infoq.com/minibooks/domain-driven-design-quickly/).
The difficulty will be further **reduced if the entire codebase is consistent**
(at least in terms of writing compliant code), as they can just emulate the style.

## Decision

We will **apply DDD principles with adaptations that reflect the nature of open-source development**. Specifically:
- **Decisions are shaped by collective input**, with the user community and contributors considered stakeholders,
  while the repository owner is the key stakeholder.
- **Domain understanding evolves through decentralized community discussions** across various platforms
  such as issue trackers and pull requests, rather than collaboration with traditional domain experts.
- **The codebase acts as the sole source of truth for the domain model and ubiquitous language**,
  with definitions provided through code comments on domain classes.
- **The repository owner mediates conflicts and provides final approval for changes impacting core domain concepts**.
  More domain stewards may be nominated by the repository owner, at their discretion.

Like a typical DDD project builds its language around existing manual business processes,
we will base ours around a hypothetical manual backup process,
involving steps such as identifying data to back up, selecting storage solutions, and verifying backup integrity.

## Consequences

### Positive

- **DDD principles ensure domain clarity, guiding development to avoid arbitrary abstractions**,
  even without traditional stakeholders and domain experts.
- **Community-driven discussions provide valuable insights**, shaping the domain based on actual usage and feedback.
- **Separating the domain from infrastructural code makes it more portable**, making it easier to evolve surrounding
technologies without impacting the core functionality.
- **A focus on the domain increases the likelihood of solving real-world needs.**
- **A focus on the domain makes the user interface easier to navigate and understand.**

### Negative

- **Decentralized discussions make alignment more challenging**, 
  requiring active effort to keep the domain model cohesive.
- **Without dedicated domain experts and guided domain discussions, interpretations of domain concepts may vary**,
  leading to inconsistencies.
- **Concept definitions may shift as project priorities evolve**, potentially disrupting domain consistency.
- **Contributors may be unfamiliar with DDD** imposing either burden on them to learn it, or on the reviewers to
help them refactor the code towards compliance.