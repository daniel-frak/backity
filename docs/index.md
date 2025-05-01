---
layout: default
title: Getting started
nav_order: 1
---

# Contents
{:.no_toc}

* TOC
{:toc}

# General

This project uses Domain-Driven Design - **make sure you're
[familiar with the basics of DDD](https://www.infoq.com/minibooks/domain-driven-design-quickly/)** before contributing
to it.

The project uses the **Ports & Adapters (aka hexagonal) architecture**. Learn more at:

- ["Ports & Adapters (aka hexagonal) architecture explained" - codesoapbox.dev](https://codesoapbox.dev/ports-adapters-aka-hexagonal-architecture-explained/)
- ["Hexagonal architecture" - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)

## Prerequisites

- JDK 21+
- [Docker](https://docs.docker.com/get-docker/),
- [Docker-Compose](https://docs.docker.com/compose/install/) (for dependencies when running the application locally)

## Dependencies (for running the application locally)

While Backity can work on its own, you may want to mock some services, such as the GOG API, or an S3 server.
These mock dependencies are provided as Docker containers.

To start the common mock dependencies, use the following command in the root of this repository:

```shell
docker compose -f docker/e2e/docker-compose.yml --env-file docker/e2e/.env up -d
```

This will start a mock GOG API.

Optionally, to start a mock S3 server, add the `s3` profile:

```shell
docker compose --profile s3 -f docker/e2e/docker-compose.yml --env-file docker/e2e/.env up -d
```

## Profiles summary

The project can be built and run with various different profiles to allow for flexible configuration.

To build the application with the selected Maven profiles, use the `-P` flag, e.g.:
```shell
mvn clean verify -Psome-maven-profile-1 -Psome-maven-profile-2
```

To run the application with the selected Spring profiles, use the `-Dspring-boot.run.profiles` flag, e.g.:

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=some-spring-profile1,some-spring-profile2
```

Below, you'll find a short summary of the available profiles.

### Maven profiles (compile-time)

* `sonar-cloud` - for CI code analysis
* `frontend-pre-sonar` - for including code coverage reports from the `frontend` module during a sonar analysis
* `frontend-sonar` - for running only a sonar analysis for the `frontend module`
* `angular-client-code-gen` - for [generating client code](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/)

### Spring profiles (run-time)

* `dev` - for local development. Allows things like handling requests from `http://localhost:4200/`.
* `dev-provider-api` - for local development. Uses mock APIs for Game Providers
  (provided by the `mockserver` Docker container)
* `dev-s3` - for local development. Uses a mock S3 provider for file storage
  (provided by the `localstack` Docker container)
* `angular-client-code-gen` - special profile used for
  [client code generation](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/).
  Applied automatically when the `angular` Maven profile is enabled.

## API documentation

First, build and run the application. Then you'll be able to reach the API docs.

### Swagger

The Swagger UI page: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

### OpenAPI

The OpenAPI description is available at the following urls:

* [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) - in `json` format
* [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) - in `yml` format

## Client code generation

To run [client code generation](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/)
using the `openapi-generator-maven-plugin`, execute the following command:

```shell
mvn clean verify -Pangular-client-code-gen -DskipTests
```

The application will be started so that the API specification can be obtained from the Open API endpoint.

The generated code will be available in the `frontend/src/main/angular/src/backend` directory.
**Don't edit those files manually.**

## Working with frontend on a local environment

If you want to see how changes you make in the frontend code affect the application you don't need to build it together
with the `backend` module every time. Use the following commands:

```shell
cd frontend/src/main/angular
ng serve
```

The application will become available at [http://localhost:4200/](http://localhost:4200/).
It reloads automatically, which speeds up your work.

To incorporate changes into the project, rebuild the whole application from the main project directory with:

```shell
mvn clean package
```

This will copy the compiled frontend into the backend module, as a resource.

## Running test suites

### Backend

Run unit tests with the following command in the project directory:

```shell
mvn test
```

Run all tests with the following command in the project directory:

```shell
mvn verify
```

### Frontend

Run all tests for the Angular code with:

```shell
cd frontend/src/main/angular
ng test
```

### End-to-end

Run end-to-end tests with:

```shell
# Build the project
mvn clean install

# Run the project
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev,dev-provider-api

# Run the tests
cd ../e2e
mvn test
```

## SonarQube analysis on a local environment

This project is configured so that its integration tests are taken into consideration when calculating code coverage.
SonarQube will show these tests as unit tests on the dashboard, as there is currently no native integration test support
within Sonar.

### Prerequisites

* You need to have [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.
* You need Chrome installed on your machine to run a frontend analysis with code coverage.

### Launching SonarQube on a local environment

To start a local instance of SonarQube, use the following command in the root of this repository:

```shell
docker compose -f docker/sonarqube/docker-compose.yml --env-file docker/sonarqube/.env up -d
```

You should only need to do this once.

Note that the first run may take a while before SonarQube is fully configured - you may want to check the Docker logs
for `sonarqube_first_run_setup_backend` to confirm whether the setup is finished successfully.

The Java analysis profile is stored in `docker/sonarqube/java_profile.xml` and is automatically restored when first
launching the Docker instance.

The quality gate is defined in an init script (`docker/sonarqube/import_data.sh`) and is automatically restored when
first launching the Docker instance.

The imported profile and quality gate are set as default.

Authentication is disabled by default.

The SonarQube instance will become available at http://localhost:9000.

### Full analysis

You can run analysis for the **whole project** (both backend and frontend) by running the following command from
the root of this repository:

```shell
mvn clean verify sonar:sonar -Pfrontend-pre-sonar -Ppitest-full
```

### Backend analysis

You can run a separate analysis for the **backend** module:

```shell
cd backend
mvn clean verify sonar:sonar -Ppitest-full
```

### Frontend analysis

You can run a separate analysis for the **frontend** module:

```shell
cd frontend
mvn sonar:sonar -Pfrontend-pre-sonar
```

### Verifying results

Visit the [Projects](http://localhost:9000/projects) SonarQube page and choose the right project.

## Mutation testing

Mutation testing is the act of automatically modifying existing code in small ways, then checking if our tests will
fail. This project supports Java mutation testing through [Pitest](https://pitest.org/).
Bear in mind that mutation testing may take a while.

To generate a full mutation coverage report, use the command:

```shell
mvn clean test-compile -Ppitest-full
```

Navigate to `./backend/target/pit-reports` and open `index.html` to view the report.

The most efficient way to generate a local coverage report during development is:

```shell
mvn clean test-compile -Ppitest-new-code
```

This will only analyze code that has been changed compared to the `main` Git branch.

To use Pitest as part of Continuous Integration, use the following command in the CI script:

```shell
mvn clean test-compile -Ppitest-new-code -Ppitest-strict
```

This will fail the build if the mutation threshold is below a certain value.

# Architectural Design Decisions

Whenever possible, Architectural Decision Records have been written as ArchUnit tests.
Where that was not possible, ADRs were written in text form and are available as 
[part of the developer documentation](https://daniel-frak.github.io/backity/adr/).
Make sure your code adheres to them.

# Contributing to the documentation

This documentation uses [Jekyll](https://jekyllrb.com/) via GitHub Pages
with [Just the Docs](https://just-the-docs.com/) as the theme 
and is stored in the project repository under the `docs` folder.

You can contribute to it by creating a Pull Request, just like you would with any other part of this project.