package es.codeurjc.mca.tfm.apigateway.cdct.consumers.users;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMIN_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERNAME_BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_CREDENTIALS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.INVALID_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.JWT_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.JWT_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_USERNAME_AND_PWD_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;


@PactTestFor(providerName = "UsersApiAdminV1Provider", pactVersion = PactSpecVersion.V3)
@DisplayName("Users API admin resources consumer CDCT tests")
public class UsersApiAdminConsumerCDCTTest extends AbstractUsersApiBaseConsumerCDCTTest {

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthentication(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with valid credentials")
        .path(ADMINS_AUTH_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .stringType(TOKEN_FIELD, JWT_TOKEN))
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with wrong body")
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(INVALID_POST_BODY)
        .path(ADMINS_AUTH_URL)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(USERNAME_BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidCredentials(
      PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with invalid credentials")
        .path(ADMINS_AUTH_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(INVALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(INVALID_CREDENTIALS_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminCreation(PactDslWithProvider builder) {

    return builder
        .given("A non-existent admin")
        .uponReceiving("creating with valid username and password")
        .path(ADMINS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_USERNAME_AND_PWD_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CREATED.value())
        .body(new PactDslJsonBody()
            .integerType("id", ID))
        .matchHeader(LOCATION_HEADER, usersUrl + ADMINS_BASE_URL + "/\\d$",
            usersUrl + ADMINS_BASE_URL + "/" + ID)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminCreationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("A non-existent admin")
        .uponReceiving("creating with invalid username")
        .path(ADMINS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(INVALID_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(USERNAME_BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminCreationWhenUsernameAlreadyExists(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("creating with already existent username")
        .path(ADMINS_BASE_URL)
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(ADMIN_ALREADY_EXISTS_RESPONSE)
        .toPact();
  }

  @Test
  @DisplayName("Test admin authentication")
  @PactTestFor(pactMethod = "adminAuthentication")
  void testAdminAuthentication(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    assertEquals(JWT_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test admin authentication with invalid username")
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidBody")
  void testAdminAuthenticationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(USERNAME_BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test admin authentication with invalid credentials")
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidCredentials")
  void testAdminAuthenticationWithInvalidCredentials(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(INVALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(INVALID_CREDENTIALS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test admin creation")
  @PactTestFor(pactMethod = "adminCreation")
  void testAdminCreation(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(VALID_USERNAME_AND_PWD_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CREATED.value(), httpResponse.getCode());
    assertEquals(usersUrl + ADMINS_BASE_URL + "/1",
        httpResponse.getFirstHeader(LOCATION_HEADER).getValue());
    assertEquals(CREATED_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test admin creation with invalid username")
  @PactTestFor(pactMethod = "adminCreationWithInvalidBody")
  void testAdminCreationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(USERNAME_BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @DisplayName("Test admin creation with an already existent username")
  @PactTestFor(pactMethod = "adminCreationWhenUsernameAlreadyExists")
  void testAdminCreationWithAlreadyExistentUsername(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(ADMIN_ALREADY_EXISTS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
