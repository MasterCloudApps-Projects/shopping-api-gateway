package es.codeurjc.mca.tfm.apigateway.cdct.consumers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.JWT_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.JWT_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USER_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_USERNAME_AND_PWD_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@PactTestFor(providerName = "UsersApiUserV1Provider", pactVersion = PactSpecVersion.V3)
public class UsersApiUsersConsumerCDCTTest extends AbstractUsersApiBaseConsumerCDCTTest {

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userAuthentication(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("authenticating with valid credentials")
        .path(AUTH_URL)
        .method("POST")
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .stringType("token", JWT_TOKEN))
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userAuthenticationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("authenticating with wrong body")
        .method("POST")
        .headers(HEADERS)
        .body(INVALID_POST_BODY)
        .path(AUTH_URL)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userAuthenticationWithInvalidCredentials(
      PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("authenticating with invalid credentials")
        .path(AUTH_URL)
        .method("POST")
        .headers(HEADERS)
        .body(INVALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(INVALID_CREDENTIALS_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userCreation(PactDslWithProvider builder) {

    return builder
        .given("A non-existent user")
        .uponReceiving("creating with valid username and password")
        .path(USERS_BASE_URL)
        .method("POST")
        .headers(HEADERS)
        .body(VALID_USERNAME_AND_PWD_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CREATED.value())
        .body(new PactDslJsonBody()
            .integerType("id", ID))
        .matchHeader(LOCATION_HEADER, usersUrl + USERS_BASE_URL + "/\\d$",
            usersUrl + USERS_BASE_URL + "/" + ID)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userCreationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("A non-existent user")
        .uponReceiving("creating with invalid username")
        .path(USERS_BASE_URL)
        .method("POST")
        .headers(HEADERS)
        .body(INVALID_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userCreationWhenUsernameAlreadyExists(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("creating with already existent username")
        .path(USERS_BASE_URL)
        .method("POST")
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(USER_ALREADY_EXISTS_RESPONSE)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "userAuthentication")
  void testUserAuthentication(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    assertEquals(JWT_TOKEN_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "userAuthenticationWithInvalidBody")
  void testUserAuthenticationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "userAuthenticationWithInvalidCredentials")
  void testUserAuthenticationWithInvalidCredentials(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + AUTH_URL)
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
  @PactTestFor(pactMethod = "userCreation")
  void testUserCreation(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + USERS_BASE_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_USERNAME_AND_PWD_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CREATED.value(), httpResponse.getCode());
    assertEquals(usersUrl + USERS_BASE_URL + "/1",
        httpResponse.getFirstHeader(LOCATION_HEADER).getValue());
    assertEquals(CREATED_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "userCreationWithInvalidBody")
  void testUserCreationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + USERS_BASE_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "userCreationWhenUsernameAlreadyExists")
  void testUserCreationWithAlreadyExistentUsername(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + USERS_BASE_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(USER_ALREADY_EXISTS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
