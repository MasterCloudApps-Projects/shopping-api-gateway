package es.codeurjc.mca.tfm.apigateway.unit.purchases;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CARTS_BASE_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
    properties = {"purchases.url=https://localhost:9946/api/v1"})
@DisplayName("Purchases API configuration unit tests")
@Tag("UnitTest")
@WireMockTest(httpsEnabled = true, httpsPort = 9946)
public class PurchaseApiConfigurationTest {

  private static final Long SHOPPING_CART_ID = 1652692327498L;

  private WebTestClient webClient;

  @Value("${purchases.url}")
  private String purchasesUrl;

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

    this.backendUri = URI.create(this.purchasesUrl);

  }

  @Test
  @DisplayName("Test purchases path that does not return a Location header route to purchases API resources and does not return a Location header")
  public void givenARequestWhenIsPurchasesPathAndDoesNotReturnLocationHeaderThenShouldRouteToPurchasesApiResourceAndResponseWithoutLocationHeader() {

    final String shoppingCartInfoResponse = "{\n"
        + "  \"id\": " + SHOPPING_CART_ID + ",\n"
        + "  \"userId\": 1,\n"
        + "  \"completed\": true,\n"
        + "  \"items\": [{\n"
        + "    \"productId\": 1,\n"
        + "    \"unitPrice\": 29.99,\n"
        + "    \"quantity\": 1,\n"
        + "    \"totalPrice\": 29.99\n"
        + "  }],\n"
        + "  \"totalPrice\": 29.99\n"
        + "}";

    //Stubs
    stubFor(get(urlMatching(this.backendUri.getPath() + SHOPPING_CARTS_BASE_URL + "/.*"))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withHeader(AUTHORIZATION, equalTo(BEARER_TOKEN))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(shoppingCartInfoResponse)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .get()
        .uri(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_TOKEN)
        .exchange()
        .expectStatus().isOk()
        .expectHeader()
        .doesNotExist(LOCATION_HEADER)
        .expectBody()
        .jsonPath("$.id").isEqualTo(SHOPPING_CART_ID)
        .jsonPath("$.userId").isEqualTo(1)
        .jsonPath("$.completed").isEqualTo(true)
        .jsonPath("$.items").isArray()
        .jsonPath("$.items[0].productId").isEqualTo(1)
        .jsonPath("$.items[0].unitPrice").isEqualTo(PRODUCT_PRICE)
        .jsonPath("$.items[0].quantity").isEqualTo(1)
        .jsonPath("$.items[0].totalPrice").isEqualTo(PRODUCT_PRICE)
        .jsonPath("$.items[1]").doesNotExist()
        .jsonPath("$.totalPrice").isEqualTo(PRODUCT_PRICE);
  }

  @Test
  @DisplayName("Test purchases path that returns a Location header route to purchases API resources and returns a Location header")
  public void givenARequestWhenIsPurchasesPathAndReturnsLocationHeaderThenShouldRouteToPurchasesApiResourceAndResponseWithLocationHeader() {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + SHOPPING_CARTS_BASE_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withStatus(HttpStatus.ACCEPTED.value())
            .withBody("{"
                + "\"id\":1652692327498"
                + "}")
            .withHeader(LOCATION_HEADER, this.purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + ID)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(SHOPPING_CARTS_BASE_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isAccepted()
        .expectHeader()
        .value(LOCATION_HEADER,
            is("https://localhost:" + this.port + SHOPPING_CARTS_BASE_URL + "/" + ID))
        .expectBody()
        .jsonPath("$.id").isEqualTo(1652692327498L);
  }

}
