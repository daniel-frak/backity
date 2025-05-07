---
title: Getting started
nav_order: 1
---

# Contents
{:.no_toc}

* TOC
{:toc}

# Introduction

Welcome to the Backity developer documentation!

Whether you're contributing to the backend, frontend, or documentation,
this guide will outline everything you need to know.

Backity uses **Domain-Driven Design (DDD)[^ddd]** and the **Ports & Adapters (Hexagonal) architecture**.
Before diving in, it's a good idea to become familiar with these concepts:

- ["Introduction to DDD" - InfoQ](https://www.infoq.com/minibooks/domain-driven-design-quickly/)
- ["Ports & Adapters (aka hexagonal) architecture explained" - codesoapbox.dev](https://codesoapbox.dev/ports-adapters-aka-hexagonal-architecture-explained/)
- ["Hexagonal architecture" - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)

[^ddd]: Traditional DDD emphasizes collaboration with domain experts and stakeholders.
  For an open-source tool without business stakeholders or formal domain experts, strategic patterns relying on
  structured domain exploration and negotiation are less applicable.
  DDD strategic patterns are necessarily adapted, 
  replacing formal collaboration with domain knowledge drawn from developer experience,
  real-world usage, and decentralized community discussions.

# Quick Start

## Prerequisites

Make sure you have the following installed:

- JDK (see `pom.xml` for the version)
- [Docker](https://docs.docker.com/get-docker/),
- [Docker Compose](https://docs.docker.com/compose/install/)
  (for dependencies when running the application locally, for running SonarQube)
- [NodeJs](https://nodejs.org/) and [npm](https://www.npmjs.com/)
  (for building frontend; see `frontend/src/main/angular/package.json` for versions)
- (Optional) A Chromium-based browser for frontend testing.

## Setting up common mock dependencies

To start the common mock dependencies, use the following command in the root of the repository:

```shell
docker compose -f docker/e2e/docker-compose.yml --env-file docker/e2e/.env up -d
```

See [Profiles overview](#profiles-overview) for more mock dependencies.

## Running the application

1. Build the project:
   `./mvnw clean package`
2. Start the backend:
   `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,dev-provider-api`
3. Access the application at [http://localhost:8080](http://localhost:8080).

{: .note }
> The frontend is automatically bundled into the backend jar when the project is built,
so you don't need to start the frontend unless you need it to automatically react to changes in its code.

## Backend API documentation

The Swagger UI page is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

The OpenAPI description is available at the following urls:

* [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) - in `json` format
* [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) - in `yml` format

## Frontend development

To run the Angular frontend independently for faster iteration:

```shell
cd frontend/src/main/angular
ng serve
```

Access the frontend at [http://localhost:4200](http://localhost:4200).
It reloads automatically, which speeds up your work.

## Architectural Design Decisions

Architectural Decision Records (ADRs) document the key design choices for the project.
Whenever possible, they are written as ArchUnit tests.
When not practical, they are documented in text form
and can be found [in the developer documentation](https://daniel-frak.github.io/backity/adr/).

Take a moment 
to review these before diving in to ensure your contributions align with the established design principles.

# Profiles overview

Backity supports multiple profiles for flexible configurations during development and testing.

- Use Maven profiles with `-P`,
- Use Spring profiles with `-Dspring-boot.run.profiles`,
- Use Docker Compose profiles with `--profile`.

Examples:

```shell
./mvnw clean verify -Pangular-client-code-gen
/mvnw spring-boot:run -Dspring-boot.run.profiles=dev,dev-provider-api
docker compose --profile s3 -f docker/e2e/docker-compose.yml --env-file docker/e2e/.env up -d
```

## Useful Maven profiles (compile-time)

| **Profile**               | **Description**                                                                                                                 |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `frontend-sonar`          | Runs only a sonar analysis for the `frontend module`.                                                                           |
| `angular-client-code-gen` | Generates [client code](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/) from the API specification. |

## Useful Spring profiles (run-time)

| **Profile**               | **Description**                                                                           |
|---------------------------|-------------------------------------------------------------------------------------------|
| `dev`                     | Allows things like handling requests from `http://localhost:4200/` for local development. |
| `dev-provider-api`        | Uses mock APIs for Game Providers (provided by the `mockserver` Docker container).        |
| `dev-s3`                  | Uses a mock S3 provider for file storage (provided by the `localstack` Docker container). |

## Docker Compose profiles (dependencies)

| **Profile** | **Description**          |
|-------------|--------------------------|
| `s3`        | Starts a mock S3 server. |

# Running test suites

## Unit tests (backend)

Run backend unit tests with:

```shell
cd backend
../mvnw test
````

Run all backend tests (including integration tests) with:

```shell
cd backend
../mvnw verify
````

## Frontend tests

Run Angular frontend tests with:

```shell
cd frontend/src/main/angular
ng test
````

## End-to-End (E2E) tests

Ensure the application is running, then execute:

```shell
cd e2e
./mvnw test
```

# Contributing to the documentation

This documentation uses [Jekyll](https://jekyllrb.com/) via GitHub Pages
with [Just the Docs](https://just-the-docs.com/) as the theme
and is stored in the project repository under the `docs` folder.

You can contribute to it by creating a Pull Request, just like you would with any other part of this project.