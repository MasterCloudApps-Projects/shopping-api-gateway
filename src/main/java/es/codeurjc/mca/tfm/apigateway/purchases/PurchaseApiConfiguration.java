package es.codeurjc.mca.tfm.apigateway.purchases;

import static org.springframework.cloud.gateway.filter.factory.RewriteLocationResponseHeaderGatewayFilterFactory.StripVersion.NEVER_STRIP;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * purchases API configuration.
 */
@Configuration
public class PurchaseApiConfiguration {

  private static final String LOCATION_HEADER = "Location";

  @Value("${purchases.url}")
  private String purchasesUrl;

  /**
   * Purchases API route locator bean instance.
   *
   * @param builder route locator builder.
   * @return route locator instance.
   */
  @Bean
  public RouteLocator purchaseProxyRouting(RouteLocatorBuilder builder) {

    URI backendUri = URI.create(this.purchasesUrl);
    return builder.routes()
        .route(r -> r
            .path("/shopping-carts/**")
            .filters(f -> f
                .prefixPath(backendUri.getPath())
                .rewriteLocationResponseHeader(NEVER_STRIP.name(),
                    LOCATION_HEADER,
                    null,
                    "http|https")
                .rewriteResponseHeader(LOCATION_HEADER, backendUri.getPath(), ""))
            .uri(this.purchasesUrl))
        .build();
  }

}
