package es.codeurjc.mca.tfm.apigateway.cdct.providers.products;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.NAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_PRODUCT_POST_BODY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.AbstractBaseProviderCDCTTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Value;

@Provider("ProductsApiV1Provider")
@DisplayName("Products API resources provider CDCT tests")
public class ProductsApiProviderCDCTTest extends AbstractBaseProviderCDCTTest {

  private static Integer PRODUCT_ID = null;

  private static Integer RED_SHOES_ID = null;

  private static Integer BLUE_SHOES_ID = null;

  @Value("${products.url}")
  protected String productsUrl;

  @Override
  protected String getUrl() {
    return this.productsUrl;
  }

  @State({"An unauthenticated admin"})
  public void nonAuthenticatedAdminState() {
  }

  @State({"An existent product"})
  public Map<String, String> existentProductState() throws Exception {

    if (PRODUCT_ID == null) {
      this.createProduct(ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY);
    }

    return Map.of(TOKEN_FIELD, ADMIN_TOKEN,
        ID_FIELD, PRODUCT_ID.toString(),
        NAME_FIELD, "DUPLICATED NAME"
    );
  }

  @State({"Existing products"})
  public Map<String, String> existentProductListState() throws Exception {

    // necessary to create products
    this.authenticateAdmin();

    if (BLUE_SHOES_ID == null) {
      BLUE_SHOES_ID = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
          Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
          VALID_PRODUCT_POST_BODY
              .replace("\"shoes\"", "\"blue shoes\"")
              .replace("comfortable ", "blue comfortable "));
    }

    if (RED_SHOES_ID == null) {
      RED_SHOES_ID = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
          Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
          VALID_PRODUCT_POST_BODY
              .replace("\"shoes\"", "\"red shoes\"")
              .replace("comfortable ", "red comfortable "));
    }

    return Map.of(
        "firstProductId", String.valueOf(BLUE_SHOES_ID),
        "secondProductId", String.valueOf(RED_SHOES_ID)
    );
  }

  @State({"A product to update"})
  public Map<String, String> existentProductToUpdateState() throws Exception {

    int productToUpdateId = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
        Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
        "{\n"
            + "  \"name\": \"name to update\",\n"
            + "  \"description\": \"description to update\",\n"
            + "  \"price\": 29.99,\n"
            + "  \"quantity\": 20\n"
            + "}");

    return Map.of(ID_FIELD, String.valueOf(productToUpdateId));
  }

  @State({"Non existent product"})
  public Map<String, String> nonExistentProductState() throws Exception {
    return Map.of(ID_FIELD, "999999");
  }

  protected void createProduct(String body) throws Exception {
    if (ADMIN_ID == null) {
      this.createAdmin();
    }

    if (ADMIN_TOKEN == null) {
      this.authenticateAdmin();
    }

    try {
      PRODUCT_ID = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
          Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
          body);
    } catch (Exception e) {
      throw new Exception("Error creating product");
    }
  }

}
