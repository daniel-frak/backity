---
title: 'Advanced topics'
parent: getting-started
order: 10
---

# Contents

# Local SonarQube analysis

:::note
> Considerations:
> 1. Some rules are missing in SonarQube Community compared to SonarCloud.
     This means that some violations might still show up during Pull Request analysis,
     even if local analysis reported nothing.
> 2. This project is configured so that its integration tests
     are taken into consideration when calculating code coverage.
     SonarQube will show these tests as unit tests on the dashboard, as there is currently no native integration test
     support within Sonar.

## Launching SonarQube on a local environment

Run a local instance of SonarQube with:

```shell
docker compose -f docker/sonarqube/docker-compose.yml --env-file docker/sonarqube/.env up -d
```

You should only need to do this once.

:::warning
> Note that the first run may take a while before SonarQube is fully configured - you may want to check the Docker logs
for `sonarqube_first_run_setup_backend` to confirm whether the setup is finished successfully.
>
> The Java analysis profile is stored in `docker/sonarqube/java_profile.xml` and is automatically restored when first
launching the Docker instance.
>
> The quality gate is defined in an init script (`docker/sonarqube/import_data.sh`) and is automatically restored when
first launching the Docker instance.
>
> The imported profile and quality gate are set as default.
>
> Authentication is disabled by default.

The SonarQube instance will become available at [http://localhost:9000](http://localhost:9000).

## Full analysis

You can run analysis for the **whole project** (both backend and frontend) by running the following command from
the root of this repository:

```shell
./mvnw clean verify sonar:sonar -Pfrontend-pre-sonar -Ppitest-full
```

## Backend analysis

You can run a separate analysis for the **backend** module:

```shell
cd backend
./mvnw clean verify sonar:sonar -Ppitest-full
```

## Frontend analysis

You can run a separate analysis for the **frontend** module:

```shell
cd frontend
./mvnw sonar:sonar -Pfrontend-pre-sonar
```

## Verifying results

Visit the [Projects](http://localhost:9000/projects) SonarQube page and choose the right project.

# Generating frontend client code for backend API

Frontend code for communicating with the backend is
[automatically generated](https://codesoapbox.dev/generate-client-code-from-spring-boot-using-maven/)
using the following command:

```shell
/.mvnw clean verify -Pangular-client-code-gen -DskipTests
```

The generated code is available in the `frontend/src/main/angular/src/backend` directory.
**Don't edit those files manually.**

# Mutation testing

Backity uses [Pitest](https://pitest.org/) for mutation testing.

:::note
> Mutation testing is the act of automatically modifying existing code in small ways, then checking if our tests fail.
> This helps identify areas where tests might pass but don't truly verify the expected behavior, or where tests
> are missing entirely.

Generate a full mutation coverage report with the `pitest-full` profile:

```shell
./mvnw clean test-compile -Ppitest-full
```

View the report at `./backend/target/pit-reports/index.html`.

The most efficient way to generate a local coverage report during development is with the `pitest-new-code` profile:

```shell
./mvnw clean test-compile -Ppitest-new-code
```

This will only analyze code that has been changed compared to the `main` Git branch.

:::note
> Pitest is also used as part of Continuous Integration, using the `pitest-strict` Maven profile,
which will fail the build if the mutation threshold is below a certain value.

# Debugging E2E tests

## Playwright trace files

Playwright tests are configured to save trace files to `e2e/target/playwright-traces`.
They are also uploaded as CI artifacts when the E2E test step fails. 
You can open a trace file with the following command:

```shell
# From the /e2e directory:
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI \
-Dexec.args="show-trace target/playwright-traces/trace_file_name.zip"
```

Alternatively, you can open the trace viewer and select the trace file manually:
```shell
# From the /e2e directory:
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace"
```

Alternatively, you can view the trace file on https://trace.playwright.dev.

## Debugging test flakiness

A useful way to debug test flakiness locally is to 
replace `@Test` with `@RepeatedTest(value = 100, failureThreshold = 1)` on the test method which is flaky.