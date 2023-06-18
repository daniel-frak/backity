[![Code Soapbox Logo](readme-images/codesoapbox_logo.svg)](https://codesoapbox.dev/)

<div align="center">

# Backity

![Backity Logo](readme-images/logo.svg)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=alert_status)](https://sonarcloud.io/dashboard?id=backity)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=backity)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=backity)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=bugs)](https://sonarcloud.io/dashboard?id=backity)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=coverage)](https://sonarcloud.io/dashboard?id=backity)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=backity)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=code_smells)](https://sonarcloud.io/dashboard?id=backity)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=backity&metric=sqale_index)](https://sonarcloud.io/dashboard?id=backity)

![GitHub](https://img.shields.io/github/license/daniel-frak/backity)

</div>

Backity is a web service for automatically downloading backups of your games from external clients such as GOG.

**For instructions on how to use Backity, [consult the Wiki](https://github.com/daniel-frak/backity/wiki).**

The information below is aimed at developers who want to extend this project's functionality.

---

- [Getting Started](#getting-started)
- [Profiles summary](#profiles-summary)
  * [Spring profiles](#spring-profiles)
  * [Maven profiles](#maven-profiles)
- [API documentation](#api-documentation)
  * [Swagger](#swagger)
  * [OpenAPI](#openapi)
- [Client code generation](#client-code-generation)
- [Working with frontend on a local environment](#working-with-frontend-on-a-local-environment)
- [Running test suites](#running-test-suites)
  * [Backend](#backend)
  * [Frontend](#frontend)
- [SonarQube analysis on a local environment](#sonarqube-analysis-on-local-a-environment)
  * [Prerequisites](#prerequisites)
  * [Full analysis](#full-analysis)
  * [Backend analysis](#backend-analysis)
  * [Frontend analysis](#frontend-analysis)
  * [Verifying results](#verifying-results)
- [Built With](#built-with)

## Getting Started

First, [clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository)
this repository.

Then, build it locally with:

```shell
mvn clean install
```

You can run the project with the following command:

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

As a result, you should be able to visit the home page on [http://localhost:8080/](http://localhost:8080/):

![home page screenshot](readme-images/home-page-screenshot.png)

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
* `code-coverage` - for including code coverage reports from the `backend` module during a sonar analysis
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

### Prerequisites

* You'll need Chrome installed on your machine to run a frontend analysis with code coverage.
* Read the [Boost project quality with SonarQube – local code analysis](https://keepgrowing.in/tools/boost-project-quality-with-sonarqube-local-code-analysis/)
  post to set up dependencies properly.
* Read the [How to add an Angular module built with Maven to a SonarQube analysis](https://keepgrowing.in/angular/how-to-add-an-angular-module-built-with-maven-to-a-sonarqube-analysis/) 
to learn more about analysing a multi-module maven application.

### Full analysis

You can run analysis for the **whole project** (both backend and frontend):

```shell
mvn clean verify sonar:sonar -Pfrontend-pre-sonar -Pcode-coverage -Dsonar.login=your_username -Dsonar.password=your_password
```

### Backend analysis

You can run a separate analysis for the **backend** module:

```shell
cd backend
mvn clean verify sonar:sonar -Pcode-coverage -Dsonar.login=your_username -Dsonar.password=your_password
```

### Frontend analysis

You can run a separate analysis for the **frontend** module:

```shell
mvn sonar:sonar -pl frontend -Pfrontend-pre-sonar -Dsonar.login=your_username -Dsonar.password=your_password
```

### Verifying results

Visit the [Projects](http://localhost:9000/projects) page and choose the right project. Depending on which modules were
analysed you should see one, two, or three projects.

## Built With

* [Maven](https://maven.apache.org/)
* [Spring Boot v3.1+](https://start.spring.io/)
* [Angular v12+](https://angular.io/)
* [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin)
* [springdoc-openapi](https://springdoc.org/)
* [Bootstrap v5+](https://getbootstrap.com/docs/5.0/getting-started/introduction/)
* [ng-bootstrap](https://ng-bootstrap.github.io/#/home)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>
