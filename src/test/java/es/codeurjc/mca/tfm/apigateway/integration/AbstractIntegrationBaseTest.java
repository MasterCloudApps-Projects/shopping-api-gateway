package es.codeurjc.mca.tfm.apigateway.integration;


import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERS_BASE_URL;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import es.codeurjc.mca.tfm.apigateway.testcontainers.TestContainersBase;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.netty.http.client.HttpClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AbstractIntegrationBaseTest extends TestContainersBase {

  private static final String USERNAME_AND_PWD_POST_BODY = "{"
      + "  \"username\": \"%s\","
      + "  \"password\": \"P4ssword\""
      + "}";

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
    this.webClient = WebTestClient
        .bindToServer(httpConnector)
        .baseUrl("https://localhost:" + this.port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

  }

  protected int createUser(String username) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    return this.callCreateMethod(USERS_BASE_URL, headers,
        String.format(USERNAME_AND_PWD_POST_BODY, username));
  }

  protected String authenticateUser(String username) {
    return this.callAuthenticateMethod(USERS_BASE_URL, username);
  }

  protected int createAdmin(String username) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    return this.callCreateMethod(ADMINS_BASE_URL, headers,
        String.format(USERNAME_AND_PWD_POST_BODY, username));
  }

  protected String authenticateAdmin(String username) {
    return this.callAuthenticateMethod(ADMINS_BASE_URL, username);
  }

  protected int callCreateMethod(String url, MultiValueMap<String, String> headers, String body) {
    Map<String, Integer> response = webClient
        .post()
        .uri(url)
        .headers(httpHeadersOnWebClientBeingBuilt -> {
          httpHeadersOnWebClientBeingBuilt.addAll(headers);
        })
        .bodyValue(body)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .value(LOCATION_HEADER,
            startsWith("https://localhost:" + this.port + url + "/"))
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(1, response.size());
    assertTrue(response.containsKey(ID_FIELD));

    return response.get(ID_FIELD);
  }

  private String callAuthenticateMethod(String baseUrl, String username) {
    Map<String, String> response = webClient
        .post()
        .uri(baseUrl + AUTH_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(String.format(USERNAME_AND_PWD_POST_BODY, username))
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(1, response.size());
    assertNotNull(response.get(TOKEN_FIELD));

    return response.get(TOKEN_FIELD);
  }
}
