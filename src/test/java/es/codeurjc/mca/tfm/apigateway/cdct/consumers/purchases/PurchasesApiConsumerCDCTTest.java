package es.codeurjc.mca.tfm.apigateway.cdct.consumers.purchases;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CAN_NOT_COMPLETE_SHOPPING_CART_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CAN_NOT_DELETE_ITEM_FROM_SHOPPING_CART_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CAN_NOT_DELETE_SHOPPING_CART_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CAN_NOT_SET_ITEM_TO_SHOPPING_CART_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.COMPLETED_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ITEMS_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.MISSING_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE_AND_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.QUANTITY_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SET_ITEM_BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CARTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CART_ALREADY_EXISTS_MSG;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CART_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CART_ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CART_NOT_FOUND_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOTAL_PRICE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.UNIT_PRICE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USER_ID_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
@PactTestFor(providerName = "PurchasesApiV1Provider", pactVersion = PactSpecVersion.V3)
@DisplayName("Purchases API resources consumer CDCT tests")
public class PurchasesApiConsumerCDCTTest {

  @Value("${purchases.url}")
  private String purchasesUrl;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertNotNull(mockServer);
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCreation(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("creating shopping cart")
        .path(SHOPPING_CARTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.ACCEPTED.value())
        .matchHeader(LOCATION_HEADER, purchasesUrl + SHOPPING_CARTS_BASE_URL + "/\\d+$",
            purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCreationWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("creating a shopping cart without authentication token")
        .path(SHOPPING_CARTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCreationWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("creating a shopping cart with invalid token")
        .path(SHOPPING_CARTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCreationWhenIncompleteShoppingCartAlreadyExists(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("creating shopping cart with already incomplete shopping cart")
        .path(SHOPPING_CARTS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(new PactDslJsonBody()
            .valueFromProviderState("error",
                String.format(SHOPPING_CART_ALREADY_EXISTS_MSG, "${id}"),
                String.format(SHOPPING_CART_ALREADY_EXISTS_MSG, SHOPPING_CART_ID)))
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact getShoppingCartInfo(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("A shopping cart with items")
        .uponReceiving("getting shopping cart with existing shopping cart")
        .pathFromProviderState(SHOPPING_CARTS_BASE_URL + "/${id}",
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.GET.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .valueFromProviderState(ID_FIELD, "${id}", SHOPPING_CART_ID)
            .valueFromProviderState(USER_ID_FIELD, "${userId}", ID)
            .booleanType(COMPLETED_FIELD, false)
            .decimalType(TOTAL_PRICE_FIELD, PRODUCT_PRICE * PRODUCT_QUANTITY)
            .array(ITEMS_FIELD)
            .object()
            .valueFromProviderState(PRODUCT_ID_FIELD, "${productId}", ID)
            .decimalType(UNIT_PRICE_FIELD, PRODUCT_PRICE)
            .integerType(QUANTITY_FIELD, PRODUCT_QUANTITY)
            .decimalType(TOTAL_PRICE_FIELD, PRODUCT_PRICE * PRODUCT_QUANTITY)
            .closeObject()
        )
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact getShoppingCartInfoWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("getting a shopping cart without authentication token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.GET.name())
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact getShoppingCartInfoWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("getting a shopping cart with invalid token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.GET.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact getShoppingCartInfoOfNonExistingShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("getting shopping cart of non existing shopping cart")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.GET.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(SHOPPING_CART_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeletion(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("deleting existing shopping cart")
        .pathFromProviderState(SHOPPING_CARTS_BASE_URL + "/${id}",
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.ACCEPTED.value())
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeletionWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("deleting a shopping cart without authentication token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.DELETE.name())
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeletionWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("deleting a shopping cart with invalid token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeletionOfNonExistingShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("deleting non existing shopping cart")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(SHOPPING_CART_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeletionWhenShoppingCartIsCompleted(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent complete shopping cart")
        .uponReceiving("deleting complete shopping cart")
        .pathFromProviderState(SHOPPING_CARTS_BASE_URL + "/${id}",
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(CAN_NOT_DELETE_SHOPPING_CART_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCompletion(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("A shopping cart with items")
        .uponReceiving("completing existing shopping cart")
        .pathFromProviderState(SHOPPING_CARTS_BASE_URL + "/${id}",
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.ACCEPTED.value())
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCompletionWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("completing a shopping cart without authentication token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.PATCH.name())
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCompletionWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("completing a shopping cart with invalid token")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCompletionOfNonExistingShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("completing non existing shopping cart")
        .path(SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(SHOPPING_CART_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartCompletionWithEmptyShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("completing empty shopping cart")
        .pathFromProviderState(SHOPPING_CARTS_BASE_URL + "/${id}",
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(CAN_NOT_COMPLETE_SHOPPING_CART_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItem(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("setting item to existing shopping cart")
        .pathFromProviderState(
            SHOPPING_CARTS_BASE_URL + "/${id}" + PRODUCTS_BASE_URL + "/" + PRODUCT_ID,
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(PRODUCT_PRICE_AND_QUANTITY)
        .willRespondWith()
        .status(HttpStatus.ACCEPTED.value())
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItemWithBadProductInfo(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("setting item with bad info to existing shopping cart")
        .pathFromProviderState(
            SHOPPING_CARTS_BASE_URL + "/${id}" + PRODUCTS_BASE_URL + "/" + PRODUCT_ID,
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body("{\"unitPrice\":0.00,\"quantity\":-1}")
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(SET_ITEM_BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItemWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("setting item to shopping cart without authentication token")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .body(PRODUCT_PRICE_AND_QUANTITY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItemWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("setting item to shopping cart with invalid token")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .body(PRODUCT_PRICE_AND_QUANTITY)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItemToNonExistingShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("setting item to existing shopping cart")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(PRODUCT_PRICE_AND_QUANTITY)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(SHOPPING_CART_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartSetItemToCompletedShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent complete shopping cart")
        .uponReceiving("setting item to completed shopping cart")
        .pathFromProviderState(
            SHOPPING_CARTS_BASE_URL + "/${id}" + PRODUCTS_BASE_URL + "/" + PRODUCT_ID,
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.PATCH.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .body(PRODUCT_PRICE_AND_QUANTITY)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(CAN_NOT_SET_ITEM_TO_SHOPPING_CART_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeleteItem(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent incomplete shopping cart")
        .uponReceiving("deleting item from existing shopping cart")
        .pathFromProviderState(
            SHOPPING_CARTS_BASE_URL + "/${id}" + PRODUCTS_BASE_URL + "/" + PRODUCT_ID,
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.ACCEPTED.value())
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeleteItemWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("deleting item from shopping cart without authentication token")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.DELETE.name())
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeleteItemWithInvalidToken(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user with invalid token")
        .uponReceiving("deleting item from shopping cart with invalid token")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", INVALID_BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(INVALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeleteItemFromNonExistingShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("Non existent incomplete shopping cart")
        .uponReceiving("deleting item from non existing shopping cart")
        .path(
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(SHOPPING_CART_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "PurchasesApiV1Consumer")
  public RequestResponsePact shoppingCartDeleteItemFromCompletedShoppingCart(
      PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .given("An existent complete shopping cart")
        .uponReceiving("deleting item from completed shopping cart")
        .pathFromProviderState(
            SHOPPING_CARTS_BASE_URL + "/${id}" + PRODUCTS_BASE_URL + "/" + PRODUCT_ID,
            SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .method(HttpMethod.DELETE.name())
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(CAN_NOT_DELETE_ITEM_FROM_SHOPPING_CART_RESPONSE)
        .toPact();
  }

  @Test
  @DisplayName("Test shopping cart creation")
  @PactTestFor(pactMethod = "shoppingCartCreation")
  void testShoppingCartCreation(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.ACCEPTED.value(), httpResponse.getCode());
    assertEquals(purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID,
        httpResponse.getFirstHeader(LOCATION_HEADER).getValue());

  }

  @Test
  @DisplayName("Test shopping cart creation without token")
  @PactTestFor(pactMethod = "shoppingCartCreationWithoutToken")
  void testShoppingCartCreationWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart creation invalid token")
  @PactTestFor(pactMethod = "shoppingCartCreationWithInvalidToken")
  void testShoppingCartCreationWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart creation when already exists an incomplete shopping cart")
  @PactTestFor(pactMethod = "shoppingCartCreationWhenIncompleteShoppingCartAlreadyExists")
  void testShoppingCartWithCreationAlreadyExistentIncompleteShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(String.format(SHOPPING_CART_ALREADY_EXISTS_RESPONSE, SHOPPING_CART_ID),
        IOUtils.toString(httpResponse.getEntity().getContent()));
  }

  @Test
  @DisplayName("Test get shopping cart info")
  @PactTestFor(pactMethod = "getShoppingCartInfo")
  void testGetShoppingCartInfo(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    Map<String, Object> responseBody = this.objectMapper.readValue(
        httpResponse.getEntity().getContent(), HashMap.class);
    assertEquals(responseBody.get(ID_FIELD), SHOPPING_CART_ID);
    assertEquals(responseBody.get(USER_ID_FIELD), ID);
    assertEquals(responseBody.get(COMPLETED_FIELD), false);
    List<Map<String, Object>> items = (ArrayList<Map<String, Object>>) responseBody.get(
        ITEMS_FIELD);
    assertEquals(items.get(0).get(PRODUCT_ID_FIELD), ID);
    assertEquals(items.get(0).get(UNIT_PRICE_FIELD), PRODUCT_PRICE);
    assertEquals(items.get(0).get(QUANTITY_FIELD), PRODUCT_QUANTITY);
    assertEquals(items.get(0).get(TOTAL_PRICE_FIELD), PRODUCT_PRICE * PRODUCT_QUANTITY);
    assertEquals(responseBody.get(TOTAL_PRICE_FIELD), PRODUCT_PRICE * PRODUCT_QUANTITY);

  }

  @Test
  @DisplayName("Test get shopping cart info without token")
  @PactTestFor(pactMethod = "getShoppingCartInfoWithoutToken")
  void testGatShoppingCartInfoWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get shopping cart info with invalid token")
  @PactTestFor(pactMethod = "getShoppingCartInfoWithInvalidToken")
  void testShoppingCartWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test get info of a non existing shopping cart")
  @PactTestFor(pactMethod = "getShoppingCartInfoOfNonExistingShoppingCart")
  void testGetShoppingCartInfoOfNonExistingShoppingCart(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(SHOPPING_CART_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart deletion")
  @PactTestFor(pactMethod = "shoppingCartDeletion")
  void testShoppingCartDeletion(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.ACCEPTED.value(), httpResponse.getCode());

  }

  @Test
  @DisplayName("Test shopping cart deletion without token")
  @PactTestFor(pactMethod = "shoppingCartDeletionWithoutToken")
  void testShoppingCartDeletionWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart deletion with invalid token")
  @PactTestFor(pactMethod = "shoppingCartDeletionWithInvalidToken")
  void testShoppingCartDeletionWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test deletion of a non existing shopping cart")
  @PactTestFor(pactMethod = "shoppingCartDeletionOfNonExistingShoppingCart")
  void testShoppingCartDeletionOfNonExistingShoppingCart(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(SHOPPING_CART_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart deletion when shopping cart is completed")
  @PactTestFor(pactMethod = "shoppingCartDeletionWhenShoppingCartIsCompleted")
  void testShoppingCartDeletionWithCompletedShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(CAN_NOT_DELETE_SHOPPING_CART_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));
  }

  @Test
  @DisplayName("Test shopping cart completion")
  @PactTestFor(pactMethod = "shoppingCartCompletion")
  void testShoppingCartCompletion(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.ACCEPTED.value(), httpResponse.getCode());

  }

  @Test
  @DisplayName("Test shopping cart completion without token")
  @PactTestFor(pactMethod = "shoppingCartCompletionWithoutToken")
  void testShoppingCartCompletionWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart completion with invalid token")
  @PactTestFor(pactMethod = "shoppingCartCompletionWithInvalidToken")
  void testShoppingCartCompletionWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test completion of a non existing shopping cart")
  @PactTestFor(pactMethod = "shoppingCartCompletionOfNonExistingShoppingCart")
  void testShoppingCartCompletionOfNonExistingShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(SHOPPING_CART_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test shopping cart completion when shopping cart is empty")
  @PactTestFor(pactMethod = "shoppingCartCompletionWithEmptyShoppingCart")
  void testShoppingCartCompletionWithEmptyShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(CAN_NOT_COMPLETE_SHOPPING_CART_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));
  }

  @Test
  @DisplayName("Test set item to shopping cart")
  @PactTestFor(pactMethod = "shoppingCartSetItem")
  void testShoppingCartSetItem(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(PRODUCT_PRICE_AND_QUANTITY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.ACCEPTED.value(), httpResponse.getCode());

  }

  @Test
  @DisplayName("Test set item with bad info to shopping cart")
  @PactTestFor(pactMethod = "shoppingCartSetItemWithBadProductInfo")
  void testShoppingCartSetItemWithBadInfo(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString("{\"unitPrice\":0.00,\"quantity\":-1}", ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(SET_ITEM_BAD_REQUEST_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test set item to shopping cart without token")
  @PactTestFor(pactMethod = "shoppingCartSetItemWithoutToken")
  void testShoppingCartSetItemWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .bodyString(PRODUCT_PRICE_AND_QUANTITY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test set item to shopping cart with invalid token")
  @PactTestFor(pactMethod = "shoppingCartSetItemWithInvalidToken")
  void testShoppingCartSetItemWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .bodyString(PRODUCT_PRICE_AND_QUANTITY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test set item to non existing shopping cart")
  @PactTestFor(pactMethod = "shoppingCartSetItemToNonExistingShoppingCart")
  void testShoppingCartSetItemToNonExistingShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(PRODUCT_PRICE_AND_QUANTITY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(SHOPPING_CART_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test set item to shopping cart when shopping cart is completed")
  @PactTestFor(pactMethod = "shoppingCartSetItemToCompletedShoppingCart")
  void testShoppingCartSetItemWithCompletedShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .patch(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .bodyString(PRODUCT_PRICE_AND_QUANTITY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(CAN_NOT_SET_ITEM_TO_SHOPPING_CART_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));
  }

  @Test
  @DisplayName("Test delete item from shopping cart")
  @PactTestFor(pactMethod = "shoppingCartDeleteItem")
  void testShoppingCartDeleteItem(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.ACCEPTED.value(), httpResponse.getCode());

  }

  @Test
  @DisplayName("Test delete item from shopping cart without token")
  @PactTestFor(pactMethod = "shoppingCartDeleteItemWithoutToken")
  void testShoppingCartDeleteItemWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test delete item from shopping cart with invalid token")
  @PactTestFor(pactMethod = "shoppingCartDeleteItemWithInvalidToken")
  void testShoppingCartDeleteItemWithInvalidToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, INVALID_BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(INVALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test delete item from non existing shopping cart")
  @PactTestFor(pactMethod = "shoppingCartDeleteItemFromNonExistingShoppingCart")
  void testShoppingCartDeleteItemFromNonExistingShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(SHOPPING_CART_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test delete item from shopping cart when shopping cart is completed")
  @PactTestFor(pactMethod = "shoppingCartDeleteItemFromCompletedShoppingCart")
  void testShoppingCartDeleteItemWithCompletedShoppingCart(MockServer mockServer)
      throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .delete(mockServer.getUrl() + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID
            + PRODUCTS_BASE_URL + "/" + PRODUCT_ID)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(CAN_NOT_DELETE_ITEM_FROM_SHOPPING_CART_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));
  }
}
