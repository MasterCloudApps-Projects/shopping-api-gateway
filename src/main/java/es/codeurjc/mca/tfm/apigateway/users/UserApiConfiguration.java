package es.codeurjc.mca.tfm.apigateway.users;

import static org.springframework.cloud.gateway.filter.factory.RewriteLocationResponseHeaderGatewayFilterFactory.StripVersion.NEVER_STRIP;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Users API configuration.
 */
@Configuration
public class UserApiConfiguration {

  @Value("${users.url}")
  private String usersUrl;

  /**
   * Users API route locator bean instance.
   *
   * @param builder route locator builder.
   * @return route locator instance.
   */
  @Bean
  public RouteLocator userProxyRouting(RouteLocatorBuilder builder) {

    URI backendUri = URI.create(this.usersUrl);
    return builder.routes()
        .route(r -> r
            .path("/users/auth/**")
            .filters(f -> f
                .setPath(backendUri.getPath() + "/auth"))
            .uri(this.usersUrl))
        .route(r -> r
            .path("/users/**")
            .or()
            .path("/admins/**")
            .filters(f -> f
                .prefixPath(backendUri.getPath())
                .rewriteLocationResponseHeader(NEVER_STRIP.name(), "Location", null, "http|https")
                .rewriteResponseHeader("Location", backendUri.getPath(), ""))
            .uri(this.usersUrl))
        .build();
  }

}
