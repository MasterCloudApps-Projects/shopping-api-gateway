package es.codeurjc.mca.tfm.apigateway.cdct.consumers.products;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.MISSING_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.NOT_ALLOWED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_PRODUCT_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({PactConsumerTestExt.class, SpringExtension.class})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@Tag("ConsumerCDCTTest")
@PactTestFor(providerName = "ProductsApiV1Provider", pactVersion = PactSpecVersion.V3)
@DisplayName("Products API resources consumer CDCT tests")
public class ProductsApiConsumerCDCTTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${products.url}")
  private String productsUrl;

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertNotNull(mockServer);
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact productCreation(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .uponReceiving("creating product with valid name, description, price and quantity")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(VALID_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CREATED.value())
        .body(new PactDslJsonBody()
            .integerType(ID_FIELD, ID))
        .matchHeader(LOCATION_HEADER, productsUrl + PRODUCTS_BASE_URL + "/\\d$",
            productsUrl + PRODUCTS_BASE_URL + "/" + ID)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact productCreationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .uponReceiving("creating product with invalid name")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(INVALID_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(PRODUCT_BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact productCreationWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An unauthenticated admin")
        .uponReceiving("creating a product without authentication token")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact productCreationWithWrongRole(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .uponReceiving("creating a product authenticated as user")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(VALID_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(NOT_ALLOWED_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact productCreationWhenNameAlreadyExists(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent product")
        .uponReceiving("creating product with already existent name")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(PRODUCT_ALREADY_EXISTS_RESPONSE)
        .toPact();
  }

  @Test
  @DisplayName("Test product creation")
  @PactTestFor(pactMethod = "productCreation")
  void testProductCreation(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(VALID_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CREATED.value(), httpResponse.getCode());
    assertEquals(productsUrl + PRODUCTS_BASE_URL + "/1",
        httpResponse.getFirstHeader(LOCATION_HEADER).getValue());
    assertEquals(CREATED_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test product creation with invalid name")
  @PactTestFor(pactMethod = "productCreationWithInvalidBody")
  void testProductCreationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(INVALID_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(PRODUCT_BAD_REQUEST_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test product creation without authentication")
  @PactTestFor(pactMethod = "productCreationWithoutToken")
  void testProductCreationWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(VALID_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test product creation with an authenticated user")
  @PactTestFor(pactMethod = "productCreationWithWrongRole")
  void testProductCreationWithWrongRole(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(VALID_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(NOT_ALLOWED_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test product creation when already exists a product with passed name")
  @PactTestFor(pactMethod = "productCreationWhenNameAlreadyExists")
  void testProductCreationWithAlreadyExistentName(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(PRODUCT_ALREADY_EXISTS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
