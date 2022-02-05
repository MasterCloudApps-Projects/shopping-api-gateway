package es.codeurjc.mca.tfm.apigateway.cdct.consumers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BALANCE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BALANCE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BEARER_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.CREATED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ID;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.JWT_TOKEN;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.JWT_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.MISSING_TOKEN_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.NOT_ALLOWED_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERNAME;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERNAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USER_ALREADY_EXISTS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USER_NOT_FOUND_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_USERNAME_AND_PWD_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@PactTestFor(providerName = "UsersApiUserV1Provider", pactVersion = PactSpecVersion.V3)
public class UsersApiUsersConsumerCDCTTest extends AbstractUsersApiBaseConsumerCDCTTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact userAuthentication(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("authenticating with valid credentials")
        .path(AUTH_URL)
        .method(HttpMethod.POST.name())
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
        .method(HttpMethod.POST.name())
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
        .method(HttpMethod.POST.name())
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
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_USERNAME_AND_PWD_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CREATED.value())
        .body(new PactDslJsonBody()
            .integerType(ID_FIELD, ID))
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
        .method(HttpMethod.POST.name())
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
        .method(HttpMethod.POST.name())
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.CONFLICT.value())
        .body(USER_ALREADY_EXISTS_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact getUserInfo(PactDslWithProvider builder) {

    return builder
        .given("An authenticated user")
        .uponReceiving("getting info of an existent user")
        .pathFromProviderState(USERS_BASE_URL + "/${id}", USERS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .integerType(ID_FIELD, ID)
            .stringType(USERNAME_FIELD, USERNAME)
            .decimalType(BALANCE_FIELD, BALANCE))
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact getUserInfoWithoutToken(PactDslWithProvider builder) {

    return builder
        .given("An user")
        .uponReceiving("non authenticated getting info of an existent user")
        .matchPath(USERS_BASE_URL + "/[0-9]+", USERS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(MISSING_TOKEN_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact getUserInfoOfOtherUser(PactDslWithProvider builder) {

    return builder
        .given("A different user authenticated")
        .uponReceiving("getting info of another existent user")
        .pathFromProviderState(USERS_BASE_URL + "/${id}", USERS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.FORBIDDEN.value())
        .body(NOT_ALLOWED_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiUserV1Consumer")
  public RequestResponsePact getUserInfoOfNonExistingUser(PactDslWithProvider builder) {

    return builder
        .given("An authenticated admin")
        .uponReceiving("getting info of non existent user")
        .pathFromProviderState(USERS_BASE_URL + "/${id}", USERS_BASE_URL + "/" + ID)
        .method(HttpMethod.GET.name())
        .headers(HEADERS)
        .headerFromProviderState(AUTHORIZATION, "Bearer ${token}", BEARER_TOKEN)
        .willRespondWith()
        .status(HttpStatus.NOT_FOUND.value())
        .body(USER_NOT_FOUND_RESPONSE)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "userAuthentication")
  void testUserAuthentication(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + AUTH_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
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
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
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
  @PactTestFor(pactMethod = "userCreation")
  void testUserCreation(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + USERS_BASE_URL)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
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
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
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
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.CONFLICT.value(), httpResponse.getCode());
    assertEquals(USER_ALREADY_EXISTS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "getUserInfo")
  void testGetUserInfo(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + USERS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    Map<String, Object> responseBody = this.objectMapper.readValue(
        httpResponse.getEntity().getContent(), HashMap.class);
    assertEquals(responseBody.get(ID_FIELD), ID);
    assertEquals(responseBody.get(USERNAME_FIELD), USERNAME);
    assertEquals(responseBody.get(BALANCE_FIELD), BALANCE);

  }

  @Test
  @PactTestFor(pactMethod = "getUserInfoWithoutToken")
  void testGetUserInfoWithoutToken(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + USERS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(MISSING_TOKEN_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "getUserInfoOfOtherUser")
  void testGetUserInfoOfOtherUser(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + USERS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.FORBIDDEN.value(), httpResponse.getCode());
    assertEquals(NOT_ALLOWED_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "getUserInfoOfNonExistingUser")
  void testGetUserInfoOfNonExistingUser(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .get(mockServer.getUrl() + USERS_BASE_URL + "/" + ID)
        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .setHeader(AUTHORIZATION, BEARER_TOKEN)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.NOT_FOUND.value(), httpResponse.getCode());
    assertEquals(USER_NOT_FOUND_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
