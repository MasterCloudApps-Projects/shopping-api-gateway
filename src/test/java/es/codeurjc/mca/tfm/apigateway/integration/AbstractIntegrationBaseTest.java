package es.codeurjc.mca.tfm.apigateway.integration;


import es.codeurjc.mca.tfm.apigateway.testcontainers.TestContainersBase;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AbstractIntegrationBaseTest extends TestContainersBase {

  protected WebTestClient webClient;

  @LocalServerPort
  protected int port;

  @BeforeEach
  public void setup() throws SSLException {

    SslContext sslContext = SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    HttpClient httpClient = HttpClient.create().secure(ssl -> {
      ssl.sslContext(sslContext);
    });
    ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
        httpClient);
    this.webClient = WebTestClient.bindToServer(httpConnector)
        .baseUrl("https://localhost:" + this.port).build();

  }

}
