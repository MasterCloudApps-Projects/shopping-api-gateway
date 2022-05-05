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
* [Products API](https://github.com/mca-tfm/products).
* [Purchases API](https://github.com/mca-tfm/purchases).

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
* [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/index.html):This plugin is used to release a project with Maven, saving a lot of repetitive, manual work.


## Project structure
Project is composed by the next modules:
* **.github/workflows**: contains workflows for [github actions](https://docs.github.com/en/actions)
* **api**: [openapi](https://swagger.io/specification/) definition with REST endpoints.
* **checkstyle**: contains project style for IDE plugin.
* **docker**: contains docker files
  * **init**: folder that contains mysql docker image init script [01.sql](docker/init/01.sql) to create another database (purchases).
  * **docker-compose.yml**: allows to launch the app and its necessary resources in local (MySQL database, users API and apigateway).
  * **docker-compose-dev.yml**: allows to launch the necessary resources to run the app in local (MySQL database, and users API).
  * **dockerize.sh**: script that build an app docker local image and to run it as a docker container.
* **postman**: postman collection and environments configuration.
* **src**: source code.
  * **main**:
    * **java**: java code.
      * **es.codeurjc.mca.tfm.apigateway**: parent package.
        * **products**: package containing code associated to products API.
        * **purchases**: package containing code associated to purchases API.
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
            * **products**: products API CDCT consumer tests.
            * **users**: users API CDCT consumer tests.
          * **providers**: CDCT providers tests. These tests check contract against providers images.
            * **products**: products API CDCT provider tests.
            * **users**: users API CDCT provider tests.
            * **AbstractBaseProviderCDCTTest.java**: Abstract class with common methods for provider tests. Also extends [TestContainersBase.java](src/test/java/es/codeurjc/mca/tfm/apigateway/testcontainers/TestContainersBase.java) to run necessary docker images.
        * **integration**: contains integration tests.
          * **products**: products API integration tests. 
          * **users**: users API integration tests.
        * **testcontainers**: contains base class with testcontainers config that launch [docker-compose-test](src/test/resources/docker-compose-test.yml) file.
        * **unit**: contains unit tests.
          * **products**: products API route configuration unit tests. 
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
* **users.url**: Users API url.
* **products.url**: Products API url.
* **purchases.url**: Purchases API url.

* **server.ssl.key-store-password**: Server key store
* **server.ssl.key-store**: Server key store path.
* **server.port**: Port where the app will run. Default value is `8444`.

Furthermore, you can use any of the [Spring Cloud properties](https://cloud.spring.io/spring-cloud-gateway/reference/html/appendix.html).
**NOTE:** Currently the property `spring.cloud.gateway.httpclient.ssl.useInsecureTrustManager` is enabled. So gateway trust all downstream certificates.
This is not suitable for production, and should be fixed in next steps. 

### Helm chart configurable values
The next variables are defined to use helm chart in [helm/charts/values.yaml](./helm/charts/values.yaml):
* **namespace**: K8s namespace. By default `tfm-dev-amartinm82`.
* **users.release**: Users API deployed release (necessary to know the users-service to use in k8s cluster). By default `users-develop`.
* **users.port**: Users API port. By default `3443`.
* **products.release**: Products API deployed release (necessary to know the products-service to use in k8s cluster). By default `products-develop`.
* **products.port**: Products API port. By default `3445`. 
* **purchases.release**: Purchases API deployed release (necessary to know the purchases-service to use in k8s cluster). By default `pur-dev`.
* **purchases.port**: Purchases API port. By default `8446`.
* **securityContext.runAsUser**: user which run the app in container. By default `1001`.
* **replicaCount**: number of replicas for the app. By default `1`.
* **image.repository**: app image name. By default `amartinm82/tfm-apigw`.
* **image.tag**: app image tag. By default `latest`.
* **service.type**: app service type. By default `ClusterIP`.
* **service.port**: app port. By default `3443`.
* **resources.requests.memory**: app instance requested memory. By default `256Mi`.
* **resources.requests.cpu**: app instance requested cpu. By default `250m`.
* **resources.limits.memory**: app instance limit memory. By default `512Mi`.
* **resources.limits.cpu**: app instance requested cpu. By default `500m`.

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
Run CDCT consumer first to generate contracts.
```
mvn test -PcdctConsumer
```
Run CDCT provider tests
```
mvn test -PcdctProvider
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
    * **Automatically**: Set values to `userUsername`, `adminUsername` and `productName` variables, and execute [Postman Collection Runner](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/).

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
  This environment is accessible in the URL https://apigw-tfm-dev-amartinm82.cloud.okteto.net.
* Production (PRO): productive environment. Accessible in URL https://apigw-tfm-amartinm82.cloud.okteto.net.

The mechanism used to deploy the application in any of the previous environment is via github actions, that are defined in workflows in folder [.github/workflows](.github/workflows).
### PRE
When a push is done on remote branch (or a PR), github actions jobs defined in [ci-cd.yml](.github/workflows/ci-cd.yml) will be fired. All the jobs depends o the previous one, so if one of them fails, the project won't be deployed in the PRE environment:
* **checkstyle**: Analyzes source code in the branch, if exists style errors fails.
* **tests**: run unitary, integration, and CDCT tests in the branch, and if there is any error, fails.
* **publish-image**: Publish Docker image `tfm-apigw` with tag `trunk` in [Dockerhub](https://hub.docker.com/).
* **deploy**: Deploy the previous generated image in PRE k8s cluster. For this, it uses the helm chart defined in [helm/charts](./helm/charts/) folder.

So, when we push in the main branch, because of the action execution, it results in if our code is right formatted, and works because it pass the tests, it is deployed and running on a k8s cluster of PRE environment.

### PRO

#### Generate and deploy a new release
To deploy in PRO environment is necessary to generate a new release. To do that, execute:
```
mvn -Dusername=<git_user> release:prepare
```    
It will tag the source code with the current version of [pom.xml](./pom.xml), push tag in remote repository, and bump project version (for detail see [Maven Release Plugin phases](https://maven.apache.org/maven-release/maven-release-plugin/examples/prepare-release.html))).

Due to the new tag is pushed, the workflow defined in [release.yml](.github/workflows/release.yml) is executed. It has several jobs:
* **check-tag**: Verifies if pushed tag match with package version (to avoid manually tags creation).
* **publish-package**: Depends on previous job. Publish mvn package version in github packages repository.
* **publish-release**: Depends on previous job. Publish the release in github.
* **publish-image**: Depends on previous job. Generate docker image of app, tagging it with `latest` and  `{pushed_tag}` (i.e: if we generated the tag 1.2.0. it tag the new image with 1.2.0), and publishing them in [Dockerhub](https://hub.docker.com/).
* **deploy**: Depends on previous job. It deploys application in PRO k8s cluster using `{pushed_tag}` image. For this, it uses the helm chart defined in [helm/charts](./helm/charts/) folder.

#### Deploy existing release
To deploy an existing release you can execute in github manual workflow defined in [manual-release-deploy.yml](.github/workflows/manual-release-deploy.yml).
Select main branch (the only one that exists), and introduce the release to deploy in the input. The release will be deployed in PRO environment.

### Checking application is deployed
Like in [Usage > Run application > Checking application is running](#checking-application-is-running) you can check if the application is successfully deployed using Openapi definition or Postman collection.
* **Openapi**: open `openapi.yml` content in [swagger editor](https://editor.swagger.io/) and select https://apigw-tfm-dev-amartinm82.cloud.okteto.net or https://apigw-tfm-amartinm82.cloud.okteto.net server and execute endpoints you want.
* **Postman**: select `TFM-apigw-pre-env` or `TFM-apigw-pro-env` environment variable. Execute postman collection as described in [Usage > Run application > Checking application is running](#checking-application-is-running).

## Developers
This project was developed by:

👤 [Álvaro Martín](https://github.com/amartinm82) - :incoming_envelope: [amartinm82@gmail.com](amartinm82@gmail.com)
