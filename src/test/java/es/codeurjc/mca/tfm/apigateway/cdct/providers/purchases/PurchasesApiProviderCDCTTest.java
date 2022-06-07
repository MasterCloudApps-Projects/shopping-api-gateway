package es.codeurjc.mca.tfm.apigateway.cdct.providers.purchases;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.COMPLETED_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ITEMS_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE_AND_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.SHOPPING_CARTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USER_ID_FIELD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.AbstractBaseProviderCDCTTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@Provider("PurchasesApiV1Provider")
@DisplayName("Purchases API resources provider CDCT tests")
public class PurchasesApiProviderCDCTTest extends AbstractBaseProviderCDCTTest {

  private static final long WAIT_TIME = 3000L;

  private static final int MAX_RETRIES = 5;

  private static Long SHOPPING_CART_ID = null;

  private static Long COMPLETED_SHOPPING_CART_ID = null;

  private static final Integer PRODUCT_ID = 1;

  @Value("${purchases.url}")
  protected String purchasesUrl;

  @Override
  protected String getUrl() {
    return this.purchasesUrl;
  }

  @State(value = {"Non existent incomplete shopping cart"}, action = StateChangeAction.TEARDOWN)
  public void cleanNonExistentIncompleteShoppingCartState() throws Exception {
    this.getCurrentIncompleteShoppingCartWithoutId();
    this.getShoppingCart(HttpStatus.OK.value());
    this.deleteShoppingCart();
    this.getShoppingCart(HttpStatus.NOT_FOUND.value());
  }

  @State({"An existent incomplete shopping cart"})
  public Map<String, String> existentIncompleteShoppingCartState() throws Exception {
    this.createShoppingCart();
    this.getShoppingCart(HttpStatus.OK.value());
    return Map.of(ID_FIELD, SHOPPING_CART_ID.toString());
  }

  @State({"A shopping cart with items"})
  public Map<String, String> shoppingCartWithItemsState() throws Exception {
    this.createShoppingCart();
    this.setItem();
    Map<String, Object> shoppingCart = null;
    int attempts = 1;
    do {
      this.getShoppingCart(HttpStatus.OK.value());
      if (shoppingCart != null &&
          !((ArrayList<Map<String, Object>>) shoppingCart.get(ITEMS_FIELD)).isEmpty()) {
        break;
      }
      attempts++;
      Thread.sleep(WAIT_TIME);
    } while (attempts <= MAX_RETRIES);

    return Map.of(ID_FIELD, SHOPPING_CART_ID.toString(),
        USER_ID_FIELD, USER_ID.toString(),
        PRODUCT_ID_FIELD, PRODUCT_ID.toString()
    );
  }

  @State({"An existent complete shopping cart"})
  public Map<String, String> completedShoppingCartState() throws Exception {
    this.createShoppingCart();
    this.setItem();
    this.completeShoppingCart();
    Map<String, Object> shoppingCart = null;
    int attempts = 1;
    do {
      shoppingCart = this.getShoppingCart(HttpStatus.OK.value());
      if (shoppingCart != null && (Boolean) shoppingCart.get(COMPLETED_FIELD)) {
        COMPLETED_SHOPPING_CART_ID = SHOPPING_CART_ID;
        SHOPPING_CART_ID = null;
        break;
      }
      attempts++;
      Thread.sleep(WAIT_TIME);
    } while (attempts <= MAX_RETRIES);

    return Map.of(ID_FIELD, COMPLETED_SHOPPING_CART_ID.toString());
  }

  @State(value = {"An existent incomplete shopping cart", "A shopping cart with items"},
      action = StateChangeAction.TEARDOWN)
  public void cleanExistentIncompleteShoppingCartState() throws Exception {
    this.deleteShoppingCart();
    this.getShoppingCart(HttpStatus.NOT_FOUND.value());
  }

  private void createShoppingCart() throws Exception {
    int attempts = 1;
    do {
      try {
        HttpPost postMethod = new HttpPost(this.purchasesUrl + SHOPPING_CARTS_BASE_URL);
        postMethod.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);

        String locationHeader;
        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
          if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.ACCEPTED.value()) {
            locationHeader = httpResponse.getHeaders(LOCATION)[0].getValue();
          } else {
            throw new Exception("POST method create doesn't return ACCEPTED status code");
          }
        }
        SHOPPING_CART_ID = Long.valueOf(locationHeader.split(SHOPPING_CARTS_BASE_URL + "/")[1]);
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
  }

  private void deleteShoppingCart() throws Exception {
    int attempts = 1;
    do {
      try {
        HttpDelete deleteMethod = new HttpDelete(
            this.purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID);
        deleteMethod.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);

        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(deleteMethod);
          if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
            throw new Exception("POST method create doesn't return ACCEPTED status code");
          }
        }
        SHOPPING_CART_ID = null;
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
  }

  private Map<String, Object> getShoppingCart(int expectedHttpStatusCode) throws Exception {
    int attempts = 1;
    Map<String, Object> responseBody = null;
    do {
      try {
        HttpGet getMethod = new HttpGet(
            this.purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID);
        getMethod.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);

        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(getMethod);
          if (httpResponse.getStatusLine().getStatusCode() != expectedHttpStatusCode) {
            throw new Exception("GET method doesn't return status code " + expectedHttpStatusCode);
          }
          responseBody = this.objectMapper.readValue(
              httpResponse.getEntity().getContent(), HashMap.class);
        }
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
    return responseBody;
  }

  private void setItem() throws Exception {
    int attempts = 1;
    do {
      try {
        HttpPatch patchMethod = new HttpPatch(
            this.purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID +
                PRODUCTS_BASE_URL + "/" + PRODUCT_ID);
        patchMethod.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);
        patchMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        patchMethod.setEntity(new StringEntity(PRODUCT_PRICE_AND_QUANTITY));

        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(patchMethod);
          if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
            throw new Exception("PATCH method set item doesn't return ACCEPTED status code");
          }
        }
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
  }

  private void completeShoppingCart() throws Exception {
    int attempts = 1;
    do {
      try {
        HttpPatch httpPatch = new HttpPatch(
            this.purchasesUrl + SHOPPING_CARTS_BASE_URL + "/" + SHOPPING_CART_ID);
        httpPatch.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);

        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(httpPatch);
          if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.ACCEPTED.value()) {
            throw new Exception("PATCH method complete doesn't return ACCEPTED status code");
          }
        }
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
  }

  private void getCurrentIncompleteShoppingCartWithoutId() throws Exception {
    int attempts = 1;
    do {
      try {
        HttpPost postMethod = new HttpPost(this.purchasesUrl + SHOPPING_CARTS_BASE_URL);
        postMethod.setHeader(AUTHORIZATION, BEARER_PREFIX + USER_TOKEN);

        Map<String, String> errorResponse;
        try (CloseableHttpClient httpClient = this.getHttpClient()) {
          CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
          if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.CONFLICT.value()) {
            errorResponse = this.objectMapper.readValue(
                httpResponse.getEntity().getContent(), HashMap.class);
          } else {
            throw new Exception("POST method create doesn't return ACCEPTED status code");
          }
        }
        SHOPPING_CART_ID = Long.valueOf(errorResponse.get("error")
            .split("Already exists incomplete shopping cart with id=")[1]);
        break;
      } catch (Exception ex) {
        if (attempts > MAX_RETRIES) {
          throw ex;
        }
        Thread.sleep(WAIT_TIME);
        attempts++;
      }
    } while (attempts <= MAX_RETRIES);
  }

}
