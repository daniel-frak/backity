- [General](#general)
    * [Profiles summary](#profiles-summary)
        + [Spring profiles](#spring-profiles)
        + [Maven profiles](#maven-profiles)
    * [API documentation](#api-documentation)
        + [Swagger](#swagger)
        + [OpenAPI](#openapi)
    * [Client code generation](#client-code-generation)
    * [Working with frontend on a local environment](#working-with-frontend-on-a-local-environment)
    * [Running test suites](#running-test-suites)
        + [Backend](#backend)
        + [Frontend](#frontend)
    * [SonarQube analysis on a local environment](#sonarqube-analysis-on-a-local-environment)
        + [Prerequisites](#prerequisites)
        + [Launching SonarQube on a local environment](#launching-sonarqube-on-a-local-environment)
        + [Full analysis](#full-analysis)
        + [Backend analysis](#backend-analysis)
        + [Frontend analysis](#frontend-analysis)
        + [Verifying results](#verifying-results)
    * [Mutation testing](#mutation-testing)
- [Architectural decisions](#architectural-decisions)
    * [Package naming](#package-naming)
    * [Database migration using Liquibase](#database-migration-using-liquibase)
    * [Don't return NULL collections](#don-t-return-null-collections)
    * [Test Data Builders](#test-data-builders)

# General

This project uses Domain-Driven Design - **make sure you're
[familiar with the basics of DDD](https://www.infoq.com/minibooks/domain-driven-design-quickly/)** before contributing
to it.

The project utilizes the **Ports & Adapters (aka hexagonal) architecture**. Learn more at:

- ["Ports & Adapters (aka hexagonal) architecture explained" - codesoapbox.dev](https://codesoapbox.dev/ports-adapters-aka-hexagonal-architecture-explained/)
- ["Hexagonal architecture" - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)

## Profiles summary

The project can be built with various different profiles to allow for flexible configuration. Below you'll find a short
summary of the available profiles.

### Spring profiles

* `dev` - for local development. Allows things like handling requests from `http://localhost:4200/`.
* `angular` - special profile used for
  [client code generation](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/).
  Applied automatically when the `angular` Maven profile is enabled.

### Maven profiles

* `sonar-cloud` - for code analysis on push to `master`
* `frontend-pre-sonar` - for including code coverage reports from the `frontend` module during a sonar analysis
* `frontend-sonar` - for running only a sonar analysis for the `frontend module`
* `angular` - for [generating client code](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/)

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
mvn clean verify -Pangular -DskipTests
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

and visit [http://localhost:4200/](http://localhost:4200/).
The application reloads automatically which speeds up your work.

In order to incorporate changes into the project, rebuild the whole application from the main project directory with:

```shell
mvn clean package
```

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

```
cd frontend/src/main/angular
ng test
```

## SonarQube analysis on a local environment

This project is configured so that its integration tests are taken into consideration when calculating code coverage.
SonarQube will show these tests as unit tests on the dashboard, as there is currently no native integration test support
within Sonar.

### Prerequisites

* You need to have [Docker](https://docs.docker.com/get-docker/) and
  [Docker-Compose](https://docs.docker.com/compose/install/) installed.
* You need Chrome installed on your machine to run a frontend analysis with code coverage.

### Launching SonarQube on a local environment

To start a local instance of SonarQube, use the following command in the root of this repository:

```shell
docker-compose -f docker/sonarqube/docker-compose_sonar.yml --env-file docker/sonarqube/.env up -d
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

This will only analyse code that has been changed compared to the `main` Git branch.

To use Pitest as part of Continuous Integration, use the following command in the CI script:

```shell
mvn clean test-compile -Ppitest-new-code -Ppitest-strict
```

This will fail the build if the mutation threshold is below a certain value.

# Architectural decisions

## Package naming

Package names should be singular when they contain a single thing (e.g. `strategy`, `payee`, `validation`) and plural  
when they hold multiple implementations (e.g. `origins`, `bases`, `rates`).

## Database migration using Liquibase

- Migration scripts should be named `db.changelog-{currentTimestamp}_{scriptName}.xml`.
- Each changeset should be named `{orderNumber}_{changeSetName}`
- Each migration should be backwards compatible.

## Don't return NULL collections

To reduce the need for null-checks, and thus the amount of NullPointerExceptions, make sure that a method
(including `@Getter`) never returns `null` when the return type is a collection. If the property itself is `null`, then
the method should return an empty collection (e.g. `emptyList()`).

Pay close attention to `null` property values which you might receive from the client (e.g. REST API).

## Test Data Builders

This project uses the [Test Data Builder](https://wiki.c2.com/?TestDataBuilder) pattern combined with
the [Object Mother](https://wiki.c2.com/?ObjectMother) pattern. Static methods provide pre-configured builders which can
be adjusted per test. This additional level of abstraction not only makes the tests more readable, but also reduces the
scope of refactoring when an object's constructor changes.

The `@Builder` annotation is intentionally not used on the domain objects themselves, as that could allow them to
be constructed in an invalid state in production code.

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with
markdown-toc</a></i></small>