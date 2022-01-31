package es.codeurjc.mca.tfm.apigateway.testcontainers;


import java.io.File;
import java.time.Duration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractContainerBaseTest {

  public static final String MYSQL_SERVICE_NAME = "mysql_1";

  public static final int MYSQL_PORT = 3306;

  public static final String USERS_SERVICE_NAME = "users_1";

  public static final int USERS_PORT = 8443;

  @Container
  protected static final DockerComposeContainer ENVIRONMENT;

  static {
    ENVIRONMENT =
        new DockerComposeContainer(new File("docker/docker-compose-dev.yml"))
            .withExposedService(MYSQL_SERVICE_NAME, MYSQL_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(USERS_SERVICE_NAME, USERS_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
  }

}
