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

## Technologies
### Dependencies
* [Spring Boot 2.6.2](https://docs.spring.io/spring-boot/docs/2.6.2/reference/html/): open source Java-based framework that offers a fast way to build applications.
* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway#overview): library for building an API Gateway on top of Spring WebFlux.
* [Githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5): Maven plugin to configure and install local git hooks.

### Development dependencies
* [Spring Boot 2.6.2 Test dependencies](https://docs.spring.io/spring-boot/docs/2.6.2/reference/html/features.html#features.testing.test-scope-dependencies): Testing provided libraries.
  * JUnit 5: The de-facto standard for unit testing Java applications.
  * Spring Test & Spring Boot Test: Utilities and integration test support for Spring Boot applications.
  * AssertJ: A fluent assertion library.
  * Hamcrest: A library of matcher objects (also known as constraints or predicates).
  * Mockito: A Java mocking framework.
  * JSONassert: An assertion library for JSON.
  * JsonPath: XPath for JSON.


## Project structure
Project is composed by the next modules:
* **api**: [openapi](https://swagger.io/specification/) definition with REST endpoints.
* **docker**: contains docker files
  * **docker-compose-dev.yml**: allows to launch the necessary resources to run the app in local (MySQL database, and users API).
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
          * **es.codeurjc.mca.tfm**: parent package.
              * **apigateway**: contains main API Gateway class test.
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
> No docker configuration defined yet

#### Checking application is running
> TODO

## Contributing
To contribute to this project have in mind:
1. It was developed using [TBD](https://trunkbaseddevelopment.com/), so only main branch exists, and is necessary that every code pushed to remote repository is ready to be deployed in production environment.
2. In order to ensure the right style and code conventions, and that code to commit and push is ok, this project use __pre-commit and pre-push git hooks__. 
   > TODO
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
> TODO

### PRO
> TODO

### Checking application is deployed
> TODO
> 
## Developers
This project was developed by:

👤 [Álvaro Martín](https://github.com/amartinm82) - :incoming_envelope: [amartinm82@gmail.com](amartinm82@gmail.com)
