package es.codeurjc.mca.tfm.apigateway.unit.products;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_DESCRIPTION;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_NAME;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_PRODUCT_POST_BODY;
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
    properties = {"products.url=https://localhost:9945/api/v1"})
@DisplayName("Products API configuration unit tests")
@Tag("UnitTest")
@WireMockTest(httpsEnabled = true, httpsPort = 9945)
public class ProductApiConfigurationTest {

  private WebTestClient webClient;

  @Value("${products.url}")
  private String productsUrl;

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

    this.backendUri = URI.create(this.productsUrl);

  }

  @Test
  @DisplayName("Test products path that does not return a Location header route to products API resources and does not return a Location header")
  public void givenARequestWhenIsProductsPathAndDoesNotReturnLocationHeaderThenShouldRouteToProductsApiResourceAndResponseWithoutLocationHeader() {

    final String productInfoResponse = "{\n"
        + "  \"id\": 1,\n"
        + "  \"name\": \"SHOES\",\n"
        + "  \"description\": \"COMFORTABLE SHOES\",\n"
        + "  \"price\": 29.99,\n"
        + "  \"quantity\": 20\n"
        + "}";

    //Stubs
    stubFor(get(urlMatching(this.backendUri.getPath() + PRODUCTS_BASE_URL + "/.*"))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withHeader(AUTHORIZATION, equalTo(BEARER_TOKEN))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(productInfoResponse)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .get()
        .uri(PRODUCTS_BASE_URL + "/" + ID)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_TOKEN)
        .exchange()
        .expectStatus().isOk()
        .expectHeader()
        .doesNotExist(LOCATION_HEADER)
        .expectBody()
        .jsonPath("$.id").isEqualTo(ID)
        .jsonPath("$.name").isEqualTo(PRODUCT_NAME.toUpperCase())
        .jsonPath("$.description").isEqualTo(PRODUCT_DESCRIPTION.toUpperCase())
        .jsonPath("$.price").isEqualTo(PRODUCT_PRICE)
        .jsonPath("$.quantity").isEqualTo(PRODUCT_QUANTITY);
  }

  @Test
  @DisplayName("Test products path that returns a Location header route to products API resources and returns a Location header")
  public void givenARequestWhenIsProductsPathAndReturnsLocationHeaderThenShouldRouteToProductsApiResourceAndResponseWithLocationHeader() {

    //Stubs
    stubFor(post(urlEqualTo(this.backendUri.getPath() + PRODUCTS_BASE_URL))
        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(VALID_PRODUCT_POST_BODY))
        .willReturn(aResponse()
            .withStatus(HttpStatus.CREATED.value())
            .withBody(CREATED_RESPONSE)
            .withHeader(LOCATION_HEADER, this.productsUrl + PRODUCTS_BASE_URL + "/" + ID)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

    webClient
        .post()
        .uri(PRODUCTS_BASE_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(VALID_PRODUCT_POST_BODY)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .value(LOCATION_HEADER,
            is("https://localhost:" + this.port + PRODUCTS_BASE_URL + "/" + ID))
        .expectBody()
        .jsonPath("$.id").isEqualTo(ID);
  }

}
