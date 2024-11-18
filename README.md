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

**⚠️Warning: Backity is not yet production-ready! Anything may break at any time.⚠️**

The information below is aimed at developers who want to extend this project's functionality.

---

- [Getting Started](#getting-started)

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

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with
markdown-toc</a></i></small>
