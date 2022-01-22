package es.codeurjc.mca.tfm.apigateway.cdct.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UsersApiAdminV1Provider", pactVersion = PactSpecVersion.V3)
public class UsersApiAdminCDCTTest {

  private static final String ADMINS_BASE_URL = "/api/v1/admins";

  private static final String ADMINS_AUTH_URL = ADMINS_BASE_URL + "/auth";

  private static final String VALID_CREDENTIALS_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020@alumnos.urjc.es\","
      + "  \"password\": \"P4ssword\""
      + "}";

  private static final String VALID_TOKEN_RESPONSE = "{"
      + "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZSI6IkFETUlOX1JPTEUiLCJpYXQiOjE2MzczNDY5ODEsImV4cCI6MTYzNzM0NzI4MX0.3s7zdOKbrY2CTMfd4qkQbapLMId-DlQL55Il05wWAFA\""
      + "}";

  private static final String INVALID_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020\","
      + "  \"password\": \"P4ssword\""
      + "}";

  private static final String BAD_REQUEST_RESPONSE = "{"
      + "  \"error\": \"Bad request.\""
      + "}";

  private static final String INVALID_CREDENTIALS_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020@alumnos.urjc.es\","
      + "  \"password\": \"Wr0ngP4ssword\""
      + "}";

  private static final String INVALID_CREDENTIALS_RESPONSE = "{"
      + "  \"error\": \"Invalid credentials.\""
      + "}";

  private static final String INTERNAL_ERROR_RESPONSE = "{"
      + "  \"error\": \"Invalid credentials.\""
      + "}";

  private static final Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{
      "Content-Type", APPLICATION_JSON_VALUE
  });

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertNotNull(mockServer);
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthentication(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with valid credentials")
        .path(ADMINS_AUTH_URL)
        .method("POST")
        .headers(headers)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(VALID_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with wrong body")
        .method("POST")
        .headers(headers)
        .body(INVALID_POST_BODY)
        .path(ADMINS_AUTH_URL)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidCredentials(
      PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with invalid credentials")
        .path(ADMINS_AUTH_URL)
        .method("POST")
        .headers(headers)
        .body(INVALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(INVALID_CREDENTIALS_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWhenInternalErrorOccurs(
      PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with valid credentials but internal error occurs")
        .path(ADMINS_AUTH_URL)
        .method("POST")
        .headers(headers)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .headers(headers)
        .body(INTERNAL_ERROR_RESPONSE)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "adminAuthentication")
  void testAdminAuthentication(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    assertEquals(VALID_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidBody")
  void testAdminAuthenticationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidCredentials")
  void testAdminAuthenticationWithInvalidCredentials(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(INVALID_CREDENTIALS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "adminAuthenticationWhenInternalErrorOccurs")
  void testAdminAuthenticationAndInternalErrorOccurs(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpResponse.getCode());
    assertEquals(INTERNAL_ERROR_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
