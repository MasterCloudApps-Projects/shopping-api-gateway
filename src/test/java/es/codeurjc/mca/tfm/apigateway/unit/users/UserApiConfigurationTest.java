package es.codeurjc.mca.tfm.apigateway.unit.users;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADDED_BALANCE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.JWT_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.JWT_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERNAME;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_CREDENTIALS_POST_BODY;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.net.URI;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"users.url=https://localhost:9943/api/v1"})
@DisplayName("Users API configuration unit tests")
@Tag("UnitTest")
@WireMockTest(httpsEnabled = true, httpsPort = 9943)
public class UserApiConfigurationTest {

  private WebTestClient webClient;

  @Value("${users.url}")
  private String usersUrl;

  @LocalServerPort
  private int port;

  private URI backendUri;

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

    this.backendUri = URI.create(this.usersUrl);

  }


  @Test
  @DisplayName("Test users auth route to users API users auth resource")
  public void givenARequestWhenIsUsersAuthPathThenShouldRouteToUsersAuthApiUrl(
      WireMockRuntimeInfo wmRuntimeInfo) {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + AUTH_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(VALID_CREDENTIALS_POST_BODY))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(JWT_TOKEN_RESPONSE)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(USERS_BASE_URL + AUTH_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(VALID_CREDENTIALS_POST_BODY)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.token").isEqualTo(JWT_TOKEN);
  }

  @Test
  @DisplayName("Test users path that does not return a Location header route to users API users resources and does not return a Location header")
  public void givenARequestWhenIsUsersPathAndDoesNotReturnLocationHeaderThenShouldRouteToUsersApiUserResourceAndResponseWithoutLocationHeader() {

    final String userInfoResponse = "{\n"
        + "  \"id\": 1,\n"
        + "  \"username\": \"a.martinmar.2021@alumnos.urjc.es\",\n"
        + "  \"balance\": 35.8\n"
        + "}";

    //Stubs
    stubFor(get(urlMatching(this.backendUri.getPath() + USERS_BASE_URL + "/.*"))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withHeader(AUTHORIZATION, equalTo(BEARER_TOKEN))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(userInfoResponse)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .get()
        .uri(USERS_BASE_URL + "/" + ID)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_TOKEN)
        .exchange()
        .expectStatus().isOk()
        .expectHeader()
        .doesNotExist(LOCATION_HEADER)
        .expectBody()
        .jsonPath("$.id").isEqualTo(ID)
        .jsonPath("$.username").isEqualTo(USERNAME)
        .jsonPath("$.balance").isEqualTo(ADDED_BALANCE);
  }

  @Test
  @DisplayName("Test users path that returns a Location header route to users API users resources and returns a Location header")
  public void givenARequestWhenIsUsersPathAndReturnsLocationHeaderThenShouldRouteToUsersApiUserResourceAndResponseWithLocationHeader() {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + USERS_BASE_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(VALID_CREDENTIALS_POST_BODY))
        .willReturn(aResponse()
            .withStatus(HttpStatus.CREATED.value())
            .withBody(CREATED_RESPONSE)
            .withHeader(LOCATION_HEADER, this.usersUrl + USERS_BASE_URL + "/" + ID)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(USERS_BASE_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(VALID_CREDENTIALS_POST_BODY)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .value(LOCATION_HEADER,
            is("https://localhost:" + this.port + USERS_BASE_URL + "/" + ID))
        .expectBody()
        .jsonPath("$.id").isEqualTo(ID);
  }

  @Test
  @DisplayName("Test admins path that does not return a Location header route to users API admins resources and does not return a Location header")
  public void givenARequestWhenIsAdminsPathAndDoesNotReturnLocationHeaderThenShouldRouteToUsersApiAdminResourceAndResponseWithLocationHeader() {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + ADMINS_AUTH_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(VALID_CREDENTIALS_POST_BODY))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(JWT_TOKEN_RESPONSE)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(ADMINS_AUTH_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(VALID_CREDENTIALS_POST_BODY)
        .exchange()
        .expectStatus().isOk()
        .expectHeader()
        .doesNotExist(LOCATION_HEADER)
        .expectBody()
        .jsonPath("$.token").isEqualTo(JWT_TOKEN);
  }

  @Test
  public void givenARequestWhenIsAdminsPathAndReturnsLocationHeaderThenShouldRouteToUsersApiAdminResourceAndResponseWithoutLocationHeader() {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + ADMINS_BASE_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(VALID_CREDENTIALS_POST_BODY))
        .willReturn(aResponse()
            .withStatus(HttpStatus.CREATED.value())
            .withBody(CREATED_RESPONSE)
            .withHeader(LOCATION_HEADER, this.usersUrl + ADMINS_BASE_URL + "/" + ID)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(ADMINS_BASE_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(VALID_CREDENTIALS_POST_BODY)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .value(LOCATION_HEADER,
            is("https://localhost:" + this.port + ADMINS_BASE_URL + "/" + ID))
        .expectBody()
        .jsonPath("$.id").isEqualTo(ID);
  }

}
