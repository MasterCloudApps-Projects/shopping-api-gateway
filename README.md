# Master cloud apps TFM - API Gateway

## Table of contents
- [Master cloud apps TFM - API Gateway](#master-cloud-apps-tfm---api-gateway)
    - [Table of contents](#table-of-contents)
    - [Description](#description)
    - [Requirements](#requirements)
    - [Technologies](#technologies)
        - [Dependencies](#dependencies)
        - [Development dependencies](#development-dependencies)
    - [Project structure](#project-structure)
    - [Configuration](#configuration)
        - [Properties description](#properties-description)
    - [Usage](#usage)
        - [Installation](#installation)
        - [Run tests](#run-tests)
        - [Run application](#run-application)
            - [Locally](#locally)
            - [As docker container](#as-docker-container)
            - [Checking application is running](#checking-application-is-running)
    - [Contributing](#contributing)
    - [Deployment](#deployment)
        - [PRE](#pre)
        - [PRO](#pro)
        - [Checking application is deployed](#checking-application-is-deployed)
    - [Developers](#developers)

## Description
This is an API Gateway for MasterCloudApps TFM. It routes to:
* [Users API](https://github.com/mca-tfm/users).

## Requirements
The next requirements are necessary to work with this project:
* [JDK 11](https://www.oracle.com/es/java/technologies/javase/jdk11-archive-downloads.html)
* [Maven 3.6.3](https://maven.apache.org/docs/3.6.3/release-notes.html)
* [Docker](https://docs.docker.com/engine/install/)

## Technologies
### Dependencies
* [Spring Boot 2.6.2](https://docs.spring.io/spring-boot/docs/2.6.2/reference/html/): open source Java-based framework that offers a fast way to build applications.
* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway#overview): library for building an API Gateway on top of Spring WebFlux.

### Development dependencies
* [Spring Boot 2.6.2 Test dependencies](https://docs.spring.io/spring-boot/docs/2.6.2/reference/html/features.html#features.testing.test-scope-dependencies): Testing provided libraries.
  * JUnit 5: The de-facto standard for unit testing Java applications.
  * Spring Test & Spring Boot Test: Utilities and integration test support for Spring Boot applications.
  * AssertJ: A fluent assertion library.
  * Hamcrest: A library of matcher objects (also known as constraints or predicates).
  * Mockito: A Java mocking framework.
  * JSONassert: An assertion library for JSON.
  * JsonPath: XPath for JSON.
* [Githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5): Maven plugin to configure and install local git hooks.
* [Pact consumer](https://docs.pact.io/implementation_guides/jvm/consumer/junit5): JUnit 5 support for [Pact](https://docs.pact.io/) consumer tests (CDCT).
* [Apache HttpClient](https://hc.apache.org/httpcomponents-client-4.5.x/index.html): HTTP agent implementation based on HttpCore used in tests to disable SSL.
* [Testcontainers](https://www.testcontainers.org/): Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.
* [JUnit Platform Suite Engine](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine): The JUnit Platform supports the declarative definition and execution of suites of tests from any test engine using the JUnit Platform.
* [Spring Cloud Contract WireMock](https://docs.spring.io/spring-cloud-contract/docs/3.0.0-SNAPSHOT/reference/htmlsingle/#features-wiremock): Modules giving you the possibility to use [WireMock](http://wiremock.org/) with different servers by using the "ambient" server embedded in a Spring Boot application.
* [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin): Jib is a Maven plugin for building Docker and OCI images for your Java applications.


## Project structure
Project is composed by the next modules:
* **api**: [openapi](https://swagger.io/specification/) definition with REST endpoints.
* **checkstyle**: contains project style for IDE plugin.
* **docker**: contains docker files
  * **docker-compose.yml**: allows to launch the app and its necessary resources in local (MySQL database, users API and apigateway).
  * **docker-compose-dev.yml**: allows to launch the necessary resources to run the app in local (MySQL database, and users API).
  * **dockerize.sh**: script that build an app docker local image and to run it as a docker container.
* **postman**: postman collection and environments configuration.
* **src**: source code.
    * **main**:
      * **java**: java code.
        * **es.codeurjc.mca.tfm.apigateway**: parent package.
          * **users**: package containing code associated to users API.
          * **ApigatewayApplication.java**: contains main API Gateway class.
      * **resources**: application resources.
        * **application.yml**: application properties for configuration.
        * **keystore.jks**: repository of security certificates.
  * **test**: test folder.
      * **java**: java code.
          * **es.codeurjc.mca.tfm.apigateway**: parent package.
              * **cdct**: contains CDCT tests.
                * **consumers**: CDCT consumers tests. These tests generate pact contract in `target/pact`.
                  * **users**: users API CDCT consumer tests.
                * **providers**: CDCT providers tests. These tests check contract against providers images.
                  * **users**: users API CDCT provider tests.
                    * **AbstractUsersApiBaseProviderCDCTTest.java**: Abstract class that launch [src/test/resources/users-docker-compose-test.yml](src/test/resources/users-docker-compose-test.yml) to run necessary docker images.
              * **integration**: contains integration tests.
                * **users**: users API integration tests.
              * **testcontainers**: contains base class with testcontainers config that launch [docker-compose-test](src/test/resources/docker-compose-test.yml) file.
              * **unit**: contains unit tests.
                * **users**: users API route configuration unit tests. 
      * **resources**: application test resources.
        * **application-test.yml**: application properties for testing configuration.
        * **docker-compose-test.yml**: docker compose file for testing purposes without volumes.
* **LICENSE**: Apache 2 license file.
* **pom.xml**: file that contains information about the project and configuration details used by Maven to build the project.
* **README.md**: this file.

## Configuration
Project configuration is in [src/main/resources/application.yml](./src/main/resources/application.yml) file.

### Properties description
> No properties defined yet.

## Usage

### Installation
To install the project execute
```sh
mvn clean install
```

### Run tests
To run tests execute:
```
mvn test
```

#### Run Unit Tests
```
mvn test -Punit
```

#### Run Integration Tests
```
mvn test -Pit
```

#### Run CDCT Tests
```
mvn test -Pcdct
```

### Run application

#### Locally
To run application locally:
1. Up necessary services:
   ```
   docker-compose -f docker/docker-compose-dev.yml up
   ```
   Note: to stop services when they are not necessary run:
   ```
   docker-compose -f docker/docker-compose-dev.yml down
   ```
2. Execute the app:
    ```
    mvn spring-boot:run
    ```

#### As docker container
To run application in a docker container execute:
```
cd docker
./dockerize.sh
```
Note: to stop application in container when not necessary then run:
```
cd docker
docker-compose down
```

#### Checking application is running
In both cases, [locally](#locally) and [As docker container](#as-docker-container) you can use [openapi definition](./api/openapi.yml) or [Postman collection](./postman/API Gateway.postman_collection.json) to test running application.

* **Openapi**: open `openapi.yml` content in [swagger editor](https://editor.swagger.io/) and select `localhost` server and execute endpoints you want.
* **Postman**: select `TFM-apigw-local-env` environment variable. Execute postman collection:
    * **Manually**: Set values you want in the endpoint body and run it.
    * **Automatically**: Set values to `userUsername` and `adminUsername` variables, and execute [Postman Collection Runner](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/).

## Contributing
To contribute to this project have in mind:
1. It was developed using [TBD](https://trunkbaseddevelopment.com/), so only main branch exists, and is necessary that every code pushed to remote repository is ready to be deployed in production environment.
2. In order to ensure the right style and code conventions, and that code to commit and push is ok, this project use __pre-commit and pre-push git hooks__.
   This is implemented using [githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5).
    * **pre-commit:** This hook run [maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and unit tests, and if fails, changes can't be committed.
    * **pre-push:** This hook run CDCT adn integrations tests, and if fails, commits can't be pushed. 
4. The API First approach was used, so please, if is necessary to modify API, in first place you must modify and validate [openapi definition](./api/openapi.yml), and later, perform the code changes.
5. Every code you modify or add must have a test that check the right behaviour of it (As a future task we'll add sonar to ensure there is a minimum coverage).

## Deployment
This project has two available environments:
* Preproduction (PRE): Used to test the application previously to release it in a productive environment. 
  > TODO add PRE URL
* Production (PRO): productive environment. 
  > TODO add PRO URL

The mechanism used to deploy the application in any of the previous environment is via github actions, that are defined in workflows in folder [.github/workflows](.github/workflows).
### PRE
When a push is done on remote branch (or a PR), github actions jobs defined in [ci-cd.yml](.github/workflows/ci-cd.yml) will be fired. All the jobs depends o the previous one, so if one of them fails, the project won't be deployed in the PRE environment:
* **install**: Check style errors, run unitary, integration, and CDCT tests in the branch, and if there is any error fails.
* **publish-image**: Publish Docker image `tfm-apigw` with tag `trunk` in [Dockerhub](https://hub.docker.com/).
> TODO

### PRO
> TODO

### Checking application is deployed
> TODO
> 
## Developers
This project was developed by:

👤 [Álvaro Martín](https://github.com/amartinm82) - :incoming_envelope: [amartinm82@gmail.com](amartinm82@gmail.com)
