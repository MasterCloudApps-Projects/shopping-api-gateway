package es.codeurjc.mca.tfm.apigateway.integration.products;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.DESCRIPTION_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.NAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRICE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_PRICE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCT_QUANTITY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.QUANTITY_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import es.codeurjc.mca.tfm.apigateway.integration.AbstractIntegrationBaseTest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.SSLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("Products API integration tests")
public class ProductsApiTest extends AbstractIntegrationBaseTest {

  private static int PRODUCT_COUNTER = 0;

  private static final String NAME_TEMPLATE = "product %s";

  private static final String PRODUCT_REQUEST_BODY = "{"
      + "  \"name\": \"%s\","
      + "  \"description\": \"%s\","
      + "  \"price\": %.2f,\n"
      + "  \"quantity\": %d\n"
      + "}";

  private static final String DESCRIPTION = " description";

  private static String adminToken = null;

  private static String userToken = null;

  @Override
  @BeforeEach
  public void setup() throws SSLException {
    super.setup();

    if (adminToken == null) {
      String productAdminUsername = "productAdmin@mail.com";
      this.createAdmin(productAdminUsername);
      adminToken = this.authenticateAdmin(productAdminUsername);
    }

    if (userToken == null) {
      String productUserUsername = "productUser@mail.com";
      this.createUser(productUserUsername);
      userToken = this.authenticateUser(productUserUsername);
    }

  }

  @Test
  @DisplayName("Test products creation")
  public void givenProductsApiRequestWhenCreateProductThenShouldCreateProductAndReturnOkResponseAndId() {
    this.createProduct(this.generateProductName());
  }

  @Test
  @DisplayName("Test products get info as admin")
  public void givenProductsApiRequestWhenGetExistentProductInfoAsAdminThenShouldReturnOkAndInfo() {
    this.getProduct(adminToken);
  }

  @Test
  @DisplayName("Test products get info as user")
  public void givenProductsApiRequestWhenGetExistentProductInfoAsUserThenShouldReturnOkAndInfo() {
    this.getProduct(userToken);
  }

  @Test
  @DisplayName("Test products update")
  public void givenProductsApiRequestWhenUpdateExistentProductThenShouldReturnOkResponseAndUpdatedProductInfo() {
    final String name = this.generateProductName();
    int productId = this.createProduct(name);

    String updatedName = "Updated " + name;
    String updatedDescription = "Updated " + name + DESCRIPTION;

    Map<String, Object> response = webClient
        .put()
        .uri(PRODUCTS_BASE_URL + "/" + productId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_PREFIX + adminToken)
        .bodyValue(
            String.format(Locale.ENGLISH, PRODUCT_REQUEST_BODY, updatedName, updatedDescription,
                34.25, 9))
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(5, response.size());
    assertEquals(productId, response.get(ID_FIELD));
    assertEquals(updatedName.toUpperCase(), response.get(NAME_FIELD));
    assertEquals(updatedDescription.toUpperCase(), response.get(DESCRIPTION_FIELD));
    assertEquals(34.25, response.get(PRICE_FIELD));
    assertEquals(9, response.get(QUANTITY_FIELD));
  }

  @Test
  @DisplayName("Test products get list as admin")
  public void givenProductsApiRequestWhenGetProductsListAsAdminThenShouldReturnOkAndProductsList() {
    this.getProductList(adminToken);
  }

  @Test
  @DisplayName("Test products get list as user")
  public void givenProductsApiRequestWhenGetProductsListAsUserThenShouldReturnOkAndProductsList() {
    this.getProductList(userToken);
  }

  private String generateProductName() {
    PRODUCT_COUNTER++;
    return String.format(NAME_TEMPLATE, PRODUCT_COUNTER);
  }

  private int createProduct(String name) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    headers.add(AUTHORIZATION, BEARER_PREFIX + adminToken);
    return this.callCreateMethod(PRODUCTS_BASE_URL, headers,
        String.format(Locale.ENGLISH, PRODUCT_REQUEST_BODY, name, name + DESCRIPTION,
            PRODUCT_PRICE, PRODUCT_QUANTITY));
  }

  private void getProduct(String token) {
    final String name = this.generateProductName();
    int productId = this.createProduct(name);

    Map<String, Object> response = webClient
        .get()
        .uri(PRODUCTS_BASE_URL + "/" + productId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_PREFIX + token)
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(5, response.size());
    assertEquals(productId, response.get(ID_FIELD));
    assertEquals(name.toUpperCase(), response.get(NAME_FIELD));
    assertEquals(name.toUpperCase() + DESCRIPTION.toUpperCase(), response.get(DESCRIPTION_FIELD));
    assertEquals(PRODUCT_PRICE, response.get(PRICE_FIELD));
    assertEquals(PRODUCT_QUANTITY, response.get(QUANTITY_FIELD));
  }

  private void getProductList(String token) {
    int productsNumber = 3;
    Map<String, Integer> addedProducts = new HashMap<>();
    for (int i = 0; i < productsNumber; i++) {
      String productName = this.generateProductName();
      addedProducts.put(productName, this.createProduct(productName));
    }

    List<LinkedHashMap<String, Object>> response = webClient
        .get()
        .uri(PRODUCTS_BASE_URL + "/")
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, BEARER_PREFIX + token)
        .exchange()
        .expectStatus().isOk()
        .expectBody(List.class)
        .returnResult()
        .getResponseBody();

    assertTrue(response.size() >= productsNumber);
    addedProducts.entrySet().stream()
        .map(e -> {
              LinkedHashMap<String, Object> product = new LinkedHashMap<>();
              product.put(ID_FIELD, e.getValue());
              product.put(NAME_FIELD, e.getKey().toUpperCase());
              product.put(DESCRIPTION_FIELD, e.getKey().toUpperCase() + DESCRIPTION.toUpperCase());
              product.put(PRICE_FIELD, PRODUCT_PRICE);
              product.put(QUANTITY_FIELD, PRODUCT_QUANTITY);
              return product;
            }
        )
        .forEach(product -> assertTrue(response.contains(product)));

  }

}
