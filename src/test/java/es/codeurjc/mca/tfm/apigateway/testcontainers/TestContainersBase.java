package es.codeurjc.mca.tfm.apigateway.testcontainers;

import java.io.File;
import java.time.Duration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TestContainersBase {

  protected static final String MYSQL_SERVICE_NAME = "mysql_1";

  protected static final int MYSQL_PORT = 3306;

  protected static final String USERS_SERVICE_NAME = "users_1";

  protected static final int USERS_PORT = 8443;

  protected static final String DYNAMODB_SERVICE_NAME = "dynamodb_1";

  protected static final int DYNAMODB_PORT = 8000;

  protected static final String PRODUCTS_SERVICE_NAME = "products_1";

  protected static final int PRODUCTS_PORT = 3445;

  protected static final DockerComposeContainer ENVIRONMENT;

  static {
    ENVIRONMENT =
        new DockerComposeContainer(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService(MYSQL_SERVICE_NAME, MYSQL_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(USERS_SERVICE_NAME, USERS_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
            .withExposedService(DYNAMODB_SERVICE_NAME, DYNAMODB_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(PRODUCTS_SERVICE_NAME, PRODUCTS_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
    ENVIRONMENT.start();
  }

}
