package es.codeurjc.mca.tfm.apigateway.cdct.consumers.products;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BAD_PRODUCT_ID_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.DESCRIPTION_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.MISSING_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.NAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.NOT_ALLOWED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRICE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_DESCRIPTION;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_NAME;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_NOT_FOUND_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.QUANTITY_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UPDATED_PRODUCT_DESCRIPTION;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UPDATED_PRODUCT_NAME;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UPDATED_PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UPDATED_PRODUCT_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UPDATE_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_PRODUCT_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private static final String BLUE = "BLUE ";

  private static final String RED = "RED ";

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

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProducts(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Existing products")
        .uponReceiving("getting products list")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(PactDslJsonArray.newUnorderedMinArray(2)
            .object()
            .valueFromProviderState(ID_FIELD, "${firstProductId}", ID)
            .stringValue(NAME_FIELD, BLUE + PRODUCT_NAME.toUpperCase())
            .stringValue(DESCRIPTION_FIELD, BLUE + PRODUCT_DESCRIPTION.toUpperCase())
            .decimalType(PRICE_FIELD, PRODUCT_PRICE)
            .integerType(QUANTITY_FIELD, PRODUCT_QUANTITY)
            .closeObject()
            .object()
            .valueFromProviderState(ID_FIELD, "${secondProductId}", ID + 1)
            .stringValue(NAME_FIELD, RED + PRODUCT_NAME.toUpperCase())
            .stringValue(DESCRIPTION_FIELD, RED + PRODUCT_DESCRIPTION.toUpperCase())
            .decimalType(PRICE_FIELD, PRODUCT_PRICE)
            .integerType(QUANTITY_FIELD, PRODUCT_QUANTITY)
            .closeObject()
        )
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductsListWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An unauthenticated admin")
        .uponReceiving("non authenticated getting products list")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductsListWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("getting product list with invalid token")
        .path(PRODUCTS_BASE_URL)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductInfo(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent product")
        .uponReceiving("getting info of an existent product")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .valueFromProviderState(ID_FIELD, "${id}", ID)
            .valueFromProviderState(NAME_FIELD, "${name}", PRODUCT_NAME.toUpperCase())
            .stringType(DESCRIPTION_FIELD, PRODUCT_DESCRIPTION.toUpperCase())
            .decimalType(PRICE_FIELD, PRODUCT_PRICE)
            .integerType(QUANTITY_FIELD, PRODUCT_QUANTITY))
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductInfoWithBadId(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .uponReceiving("getting info of a product with bad id")
        .matchPath(PRODUCTS_BASE_URL + "/[^0-9]+", PRODUCTS_BASE_URL + "/Nan")
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(BAD_PRODUCT_ID_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductInfoWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("getting info of a product without token")
        .matchPath(PRODUCTS_BASE_URL + "/[0-9]+", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductInfoWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("getting info of product with invalid token")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact getProductInfoOfNonExistingProduct(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .uponReceiving("getting info of non existent product")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(PRODUCT_NOT_FOUND_RESPONSE)
        .toPact();
  }


  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateProduct(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .given("A product to update")
        .uponReceiving("updating an existent product")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(UPDATE_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .integerType(ID_FIELD, ID)
            .stringType(NAME_FIELD, UPDATED_PRODUCT_NAME)
            .stringType(DESCRIPTION_FIELD, UPDATED_PRODUCT_DESCRIPTION)
            .decimalType(PRICE_FIELD, UPDATED_PRODUCT_PRICE)
            .integerType(QUANTITY_FIELD, UPDATED_PRODUCT_QUANTITY)
        )
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateProductWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .uponReceiving("updating an existent product with bad name")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}",
            PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(INVALID_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(PRODUCT_BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateProductWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("non authenticated updating a product")
        .matchPath(PRODUCTS_BASE_URL + "/[0-9]+", PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .body(UPDATE_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateProductAsUser(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .uponReceiving("updating a product as user")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}",
            PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(UPDATE_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(NOT_ALLOWED_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateNonExistingProduct(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .given("Non existent product")
        .uponReceiving("updating a non existent product")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${id}",
            PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(UPDATE_PRODUCT_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(PRODUCT_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "ProductsApiV1Consumer")
  public RequestResponsePact updateProductWithAlreadyExistingName(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .given("Existing products")
        .uponReceiving("updating a product with already existent name")
        .pathFromProviderState(PRODUCTS_BASE_URL + "/${secondProductId}",
            PRODUCTS_BASE_URL + "/" + ID)
        .method(HttpMethod.PUT.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(VALID_PRODUCT_POST_BODY.replace("\"shoes\"", "\"blue shoes\""))
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

  @Test
  @DisplayName("Test get products list")
  @PactTestFor(pactMethod = "getProducts")
  void testGetProductsList(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    List<Map<String, Object>> responseBody = this.objectMapper.readValue(
        httpResponse.getEntity().getContent(), ArrayList.class);
    assertTrue(responseBody.size() >= 2);
    assertTrue((Integer) responseBody.get(0).get(ID_FIELD) > 0);
    assertTrue(responseBody.stream()
        .anyMatch(product ->
            product.get(NAME_FIELD).equals(BLUE + PRODUCT_NAME.toUpperCase()) &&
                product.get(DESCRIPTION_FIELD).equals(BLUE + PRODUCT_DESCRIPTION.toUpperCase()) &&
                product.get(PRICE_FIELD).equals(PRODUCT_PRICE) &&
                product.get(QUANTITY_FIELD).equals(PRODUCT_QUANTITY)
        ));
    assertTrue(responseBody.stream()
        .anyMatch(product ->
            product.get(NAME_FIELD).equals(RED + PRODUCT_NAME.toUpperCase()) &&
                product.get(DESCRIPTION_FIELD).equals(RED + PRODUCT_DESCRIPTION.toUpperCase()) &&
                product.get(PRICE_FIELD).equals(PRODUCT_PRICE) &&
                product.get(QUANTITY_FIELD).equals(PRODUCT_QUANTITY)
        ));
  }

  @Test
  @DisplayName("Test get products list without token")
  @PactTestFor(pactMethod = "getProductsListWithoutToken")
  void testGetProductsListWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get products list with invalid token")
  @PactTestFor(pactMethod = "getProductsListWithInvalidToken")
  void testGetProductListWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get product info")
  @PactTestFor(pactMethod = "getProductInfo")
  void testGetProductInfo(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    Map<String, Object> responseBody = this.objectMapper.readValue(
        httpResponse.getEntity().getContent(), HashMap.class);
    assertEquals(responseBody.get(ID_FIELD), ID);
    assertEquals(responseBody.get(NAME_FIELD), PRODUCT_NAME.toUpperCase());
    assertEquals(responseBody.get(DESCRIPTION_FIELD), PRODUCT_DESCRIPTION.toUpperCase());
    assertEquals(responseBody.get(PRICE_FIELD), PRODUCT_PRICE);
    assertEquals(responseBody.get(QUANTITY_FIELD), PRODUCT_QUANTITY);

  }

  @Test
  @DisplayName("Test get product info with not numeric identifier")
  @PactTestFor(pactMethod = "getProductInfoWithBadId")
  void testGetProductInfoWithBadId(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL + "/Nan")
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(BAD_PRODUCT_ID_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get product info without token")
  @PactTestFor(pactMethod = "getProductInfoWithoutToken")
  void testGetProductInfoWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get product info with invalid token")
  @PactTestFor(pactMethod = "getProductInfoWithInvalidToken")
  void testGetProductInfoWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get info of a non existing product")
  @PactTestFor(pactMethod = "getProductInfoOfNonExistingProduct")
  void testGetProductInfoOfNonExistingProduct(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(PRODUCT_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test update product")
  @PactTestFor(pactMethod = "updateProduct")
  void testUpdateProduct(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(UPDATE_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    Map<String, Object> responseBody = this.objectMapper.readValue(
        httpResponse.getEntity().getContent(), HashMap.class);
    assertEquals(responseBody.get(ID_FIELD), ID);
    assertEquals(responseBody.get(NAME_FIELD), UPDATED_PRODUCT_NAME);
    assertEquals(responseBody.get(DESCRIPTION_FIELD), UPDATED_PRODUCT_DESCRIPTION);
    assertEquals(responseBody.get(PRICE_FIELD), UPDATED_PRODUCT_PRICE);
    assertEquals(responseBody.get(QUANTITY_FIELD), UPDATED_PRODUCT_QUANTITY);

  }

  @Test
  @DisplayName("Test update product with invalid name")
  @PactTestFor(pactMethod = "updateProductWithInvalidBody")
  void testUpdateProductWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
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
  @DisplayName("Test update product without token")
  @PactTestFor(pactMethod = "updateProductWithoutToken")
  void testUpdateProductWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(UPDATE_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test update a product authenticated as user")
  @PactTestFor(pactMethod = "updateProductAsUser")
  void testUpdateProductAsUser(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(UPDATE_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(NOT_ALLOWED_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test update a non existing product")
  @PactTestFor(pactMethod = "updateNonExistingProduct")
  void testUpdateNonExistingProduct(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(UPDATE_PRODUCT_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(PRODUCT_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test update a product with already existent name")
  @PactTestFor(pactMethod = "updateProductWithAlreadyExistingName")
  void testUpdateProductWithAlreadyExistingName(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .put(mockServer.getUrl() + PRODUCTS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(VALID_PRODUCT_POST_BODY.replace("\"shoes\"", "\"blue shoes\""),
            ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(PRODUCT_ALREADY_EXISTS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
