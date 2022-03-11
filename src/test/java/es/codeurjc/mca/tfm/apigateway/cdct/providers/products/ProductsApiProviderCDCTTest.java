package es.codeurjc.mca.tfm.apigateway.cdct.providers.products;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.PRODUCTS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_PRODUCT_POST_BODY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.AbstractBaseProviderCDCTTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Value;

@Provider("ProductsApiV1Provider")
@DisplayName("Products API resources provider CDCT tests")
public class ProductsApiProviderCDCTTest extends AbstractBaseProviderCDCTTest {

  private static Integer PRODUCT_ID = null;

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
        ID_FIELD, PRODUCT_ID.toString()
    );
  }

  @State({"An authenticated user with existing products"})
  public Map<String, String> existentProductListState() throws Exception {

    // necessary to create products
    this.authenticateAdmin();

    int shoesId = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
        Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
        VALID_PRODUCT_POST_BODY
            .replace("\"SHOES\"", "\"BLUE SHOES\"")
            .replace("COMFORTABLE ", "BLUE COMFORTABLE "));
    int redShoesId = this.callCreateMethod(this.productsUrl + PRODUCTS_BASE_URL,
        Map.of(AUTHORIZATION, BEARER_PREFIX + ADMIN_TOKEN),
        VALID_PRODUCT_POST_BODY
            .replace("\"SHOES\"", "\"RED SHOES\"")
            .replace("COMFORTABLE ", "RED COMFORTABLE "));

    Map<String, String> map = new HashMap<>();
    map.putAll(this.authenticatedUserState());
    map.put("firstProductId", String.valueOf(shoesId));
    map.put("secondProductId", String.valueOf(redShoesId));
    return map;
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
