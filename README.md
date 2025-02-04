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
- [Configuration](#configuration)
  * [Basic configuration](#basic-configuration)
  * [Advanced configuration](#advanced-configuration)
  * [S3 support](#s3-support)
- [Application behavior](#application-behavior)

## Getting Started

First, [clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository)
this repository.

Then, build it locally with:

```shell
mvn clean install
```

You can run the project with the following command:

```shell
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

As a result, you should be able to visit the home page on [http://localhost:8080/](http://localhost:8080/):

![home page screenshot](readme-images/home-page-screenshot.png)

## Configuration

### Basic configuration

The following basic properties can be customized:

- `backity.default-path-template` - the template to use when constructing the file download path. 
  Available placeholders:
  - `{GAME_PROVIDER_ID}` - the ID of the game provider, e.g., "GOG"
  - `{TITLE}` - the game title
  - `{FILENAME}` - the full name of the file being downloaded

### Advanced configuration

The following advanced properties can be customized:

- `backity.file-download-queue-scheduler` - how often the file download queue should be checked
- `backity.gog-auth-scheduler` - how often the system should check if GOG authentication should be refreshed 

### S3 support

By default, Backity uses the local file system to store files.

To enable S3 support, `backity.filesystem.s3.enabled` must be set to `true`.

Further properties should also be configured:
- `backity.filesystem.s3.bucket` - the bucket to use for storing game files
- `spring.cloud.aws.s3.endpoint` - the S3 endpoint
- `spring.cloud.aws.s3.region.static` - the S3 region
- `spring.cloud.aws.credentials.access-key` - optionally, the access key
- `spring.cloud.aws.credentials.secret-key` - optionally, the secret key

Check the [Spring Cloud AWS documentation](https://docs.awspring.io/spring-cloud-aws/docs/3.0.0-M1/reference/html/index.html)
for more advanced configuration options.

The following optional properties are also available:
- `backity.filesystem.s3.buffer-size-in-bytes` - the buffer size for multipart uploads
  (see: [AWS S3 multipart upload limits](https://docs.aws.amazon.com/AmazonS3/latest/userguide/qfacts.html))

## Application Behavior

The following information is useful to know about the application's functionality:

- If downloading a file would result in overwriting an existing file, an exception will be thrown.
  This helps to protect the existing file
  and prevents multiple Game File aggregates from pointing to the same physical file.


<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with
markdown-toc</a></i></small>
