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

  protected static final DockerComposeContainer ENVIRONMENT;

  static {
    ENVIRONMENT =
        new DockerComposeContainer(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService(MYSQL_SERVICE_NAME, MYSQL_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(USERS_SERVICE_NAME, USERS_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
    ENVIRONMENT.start();
  }

}
