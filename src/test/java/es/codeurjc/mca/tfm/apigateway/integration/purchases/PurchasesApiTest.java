package es.codeurjc.mca.tfm.apigateway.integration.purchases;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.COMPLETED_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ITEMS_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE_AND_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CARTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOTAL_PRICE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USER_ID_FIELD;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import es.codeurjc.mca.tfm.apigateway.integration.AbstractIntegrationBaseTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("Purchases API integration tests")
public class PurchasesApiTest extends AbstractIntegrationBaseTest {

  private static final long WAIT_TIME = 3000L;

  private static final int MAX_RETRIES = 5;

  private static Integer userId = null;

  private static String userToken = null;

  private static Long shoppingCartId = null;

  @Override
  @BeforeEach
  public void setup() throws SSLException {
    super.setup();

    if (userToken == null) {
      String purchasesUserUsername = "purchasesUser@mail.com";
      userId = this.createUser(purchasesUserUsername);
      userToken = this.authenticateUser(purchasesUserUsername);
    }

    if (shoppingCartId != null) {
      try {
        this.deleteShoppingCart();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  @Test
  @DisplayName("Test shopping cart creation")
  public void givenPurchasesApiRequestWhenCreateShoppingCartThenShouldReturnAccepted()
      throws InterruptedException {
    this.createShoppingCart();
  }

  @Test
  @DisplayName("Test shopping cart get info")
  public void givenPurchasesApiRequestWhenGetExistentShoppingCartInfoThenShouldReturnOkAndInfo()
      throws InterruptedException {
    this.createShoppingCart();
    Map<String, Object> response = this.getShoppingCart();
    assertEquals(5, response.size());
    assertEquals(shoppingCartId, response.get(ID_FIELD));
    assertEquals(userId, response.get(USER_ID_FIELD));
    assertFalse((Boolean) response.get(COMPLETED_FIELD));
    assertTrue(((List) response.get(ITEMS_FIELD)).isEmpty());
    assertEquals(0.0, response.get(TOTAL_PRICE_FIELD));
  }

  @Test
  @DisplayName("Test shopping cart deletion")
  public void givenPurchasesApiRequestWhenDeleteExistentShoppingCartThenShouldReturnAccepted()
      throws InterruptedException {
    this.createShoppingCart();
    this.deleteShoppingCart();
  }

  @Test
  @DisplayName("Test set item to shopping cart")
  public void givenPurchasesApiRequestWhenSetItemToExistentShoppingCartThenShouldReturnAccepted()
      throws InterruptedException {
    this.createShoppingCart();
    this.setItemToShoppingCart();
  }

  @Test
  @DisplayName("Test shopping cart completion")
  public void givenPurchasesApiRequestWhenCompleteExistentShoppingCartThenShouldReturnAccepted()
      throws InterruptedException {
    this.createShoppingCart();
    this.setItemToShoppingCart();
    this.completeShoppingCart();
  }

  @Test
  @DisplayName("Test delete item from shopping cart")
  public void givenPurchasesApiRequestWhenDeleteItemFromExistentShoppingCartThenShouldReturnAccepted()
      throws InterruptedException {
    this.createShoppingCart();
    this.deleteItemFromShoppingCart();
  }

  private void createShoppingCart() throws InterruptedException {
    int attempts = 1;
    do {
      try {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER_PREFIX + userToken);
        HttpHeaders responseHeaders = webClient
            .post()
            .uri(SHOPPING_CARTS_BASE_URL)
            .headers(httpHeadersOnWebClientBeingBuilt -> {
              httpHeadersOnWebClientBeingBuilt.addAll(headers);
            })
            .exchange()
            .expectStatus().isAccepted()
            .expectHeader()
            .value(LOCATION_HEADER,
                startsWith("https://localhost:" + this.port + SHOPPING_CARTS_BASE_URL + "/"))
            .expectBody(HashMap.class)
            .returnResult()
            .getResponseHeaders();

        shoppingCartId = Long.valueOf(responseHeaders.getLocation().getPath()
            .split(SHOPPING_CARTS_BASE_URL + "/")[1]);
        break;
      } catch (AssertionError assertionError) {
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (attempts <= MAX_RETRIES);
  }

  private Map<String, Object> getShoppingCart() throws InterruptedException {
    Map<String, Object> response = null;
    int attempts = 1;
    do {
      try {
        response = webClient
            .get()
            .uri(SHOPPING_CARTS_BASE_URL + "/" + shoppingCartId)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, BEARER_PREFIX + userToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody(HashMap.class)
            .returnResult()
            .getResponseBody();
      } catch (AssertionError assertionError) {
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (response == null && attempts <= MAX_RETRIES);
    return response;
  }

  private void deleteShoppingCart() throws InterruptedException {
    int attempts = 1;
    do {
      try {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER_PREFIX + userToken);
        webClient
            .delete()
            .uri(SHOPPING_CARTS_BASE_URL + "/" + shoppingCartId)
            .headers(httpHeadersOnWebClientBeingBuilt -> {
              httpHeadersOnWebClientBeingBuilt.addAll(headers);
            })
            .exchange()
            .expectStatus().isAccepted();
        shoppingCartId = null;
        break;
      } catch (AssertionError assertionError) {
        System.out.println("INTENTO " + attempts);
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (attempts <= MAX_RETRIES);
  }

  private void setItemToShoppingCart() throws InterruptedException {
    int attempts = 1;
    do {
      try {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER_PREFIX + userToken);
        webClient
            .patch()
            .uri(SHOPPING_CARTS_BASE_URL + "/" + shoppingCartId + PRODUCTS_BASE_URL + "/"
                + PRODUCT_ID)
            .headers(httpHeadersOnWebClientBeingBuilt -> {
              httpHeadersOnWebClientBeingBuilt.addAll(headers);
            })
            .bodyValue(PRODUCT_PRICE_AND_QUANTITY)
            .exchange()
            .expectStatus().isAccepted();
        break;
      } catch (AssertionError assertionError) {
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (attempts <= MAX_RETRIES);
  }

  private void completeShoppingCart() throws InterruptedException {
    int attempts = 1;
    do {
      try {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER_PREFIX + userToken);
        webClient
            .patch()
            .uri(SHOPPING_CARTS_BASE_URL + "/" + shoppingCartId)
            .headers(httpHeadersOnWebClientBeingBuilt -> {
              httpHeadersOnWebClientBeingBuilt.addAll(headers);
            })
            .exchange()
            .expectStatus().isAccepted();
        break;
      } catch (AssertionError assertionError) {
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (attempts <= MAX_RETRIES);
  }

  private void deleteItemFromShoppingCart() throws InterruptedException {
    int attempts = 1;
    do {
      try {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        headers.add(AUTHORIZATION, BEARER_PREFIX + userToken);
        webClient
            .delete()
            .uri(SHOPPING_CARTS_BASE_URL + "/" + shoppingCartId + PRODUCTS_BASE_URL + "/"
                + PRODUCT_ID)
            .headers(httpHeadersOnWebClientBeingBuilt -> {
              httpHeadersOnWebClientBeingBuilt.addAll(headers);
            })
            .exchange()
            .expectStatus().isAccepted();
        break;
      } catch (AssertionError assertionError) {
        if (attempts > MAX_RETRIES) {
          throw assertionError;
        }
        Thread.sleep(WAIT_TIME);
      }
      attempts++;
    } while (attempts <= MAX_RETRIES);
  }

}
