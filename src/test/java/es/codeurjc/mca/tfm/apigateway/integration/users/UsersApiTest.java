package es.codeurjc.mca.tfm.apigateway.integration.users;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADDED_BALANCE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADD_BALANCE_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BALANCE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.LOCATION_HEADER;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERNAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERS_BASE_URL;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import es.codeurjc.mca.tfm.apigateway.integration.AbstractIntegrationBaseTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Users API integration tests")
public class UsersApiTest extends AbstractIntegrationBaseTest {

  private static int USER_COUNTER = 0;

  private static final String USERNAME_TEMPLATE = "user%s@mail.com";

  private static final String USERNAME_AND_PWD_POST_BODY = "{"
      + "  \"username\": \"%s\","
      + "  \"password\": \"P4ssword\""
      + "}";

  @Test
  @DisplayName("Test users creation")
  public void givenUsersApiRequestWhenCreateUserThenShouldCreateUserAndReturnOkResponseAndId() {
    this.createUser(this.generateUsername());
  }

  @Test
  @DisplayName("Test users authentication")
  public void givenUsersApiRequestWhenAuthenticateExistentUserThenShouldReturnOkResponseAndToken() {
    final String username = this.generateUsername();
    this.createUser(username);
    this.authenticateUser(username);
  }

  @Test
  @DisplayName("Test users get info")
  public void givenUsersApiRequestWhenGetExistentUserInfoThenShouldReturnOkResponseAndUserInfo() {
    final String username = this.generateUsername();
    int userId = this.createUser(username);
    String userToken = this.authenticateUser(username);

    Map<String, Object> response = webClient
        .get()
        .uri(USERS_BASE_URL + "/" + userId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, "Bearer " + userToken)
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(3, response.size());
    assertEquals(userId, response.get(ID_FIELD));
    assertEquals(username, response.get(USERNAME_FIELD));
    assertEquals(0, response.get(BALANCE_FIELD));
  }

  @Test
  @DisplayName("Test users add balance info")
  public void givenUsersApiRequestWhenAddBalanceToExistentUserInfoThenShouldReturnOkResponseAndUserInfo() {
    final String username = this.generateUsername();
    int userId = this.createUser(username);
    String userToken = this.authenticateUser(username);

    Map<String, Object> response = webClient
        .post()
        .uri(USERS_BASE_URL + "/" + userId + "/" + BALANCE_FIELD)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, "Bearer " + userToken)
        .bodyValue(ADD_BALANCE_POST_BODY)
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(3, response.size());
    assertEquals(userId, response.get(ID_FIELD));
    assertEquals(username, response.get(USERNAME_FIELD));
    assertEquals(ADDED_BALANCE, response.get(BALANCE_FIELD));
  }

  @Test
  @DisplayName("Test admins creation")
  public void givenUsersApiRequestWhenCreateAdminThenShouldCreateAdminAndReturnOkResponseAndId() {
    this.createAdmin(this.generateUsername());
  }

  @Test
  @DisplayName("Test admins authentication")
  public void givenUsersApiRequestWhenAuthenticateExistentAdminThenShouldReturnOkResponseAndToken() {
    final String username = this.generateUsername();
    this.createAdmin(username);
    this.authenticateAdmin(username);
  }

  @Test
  @DisplayName("Test users get info as admin")
  public void givenUsersApiRequestWhenGetExistentUserInfoAsAdminThenShouldReturnOkResponseAndUserInfo() {
    final String username = this.generateUsername();
    int userId = this.createUser(username);
    final String adminUsername = this.generateUsername();
    this.createAdmin(adminUsername);
    String adminToken = this.authenticateAdmin(adminUsername);

    Map<String, Object> response = webClient
        .get()
        .uri(USERS_BASE_URL + "/" + userId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, "Bearer " + adminToken)
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(3, response.size());
    assertEquals(userId, response.get(ID_FIELD));
    assertEquals(username, response.get(USERNAME_FIELD));
    assertEquals(0, response.get(BALANCE_FIELD));
  }

  @Test
  @DisplayName("Test users add balance info")
  public void givenUsersApiRequestWhenAddBalanceToExistentUserInfoAsAdminThenShouldReturnOkResponseAndUserInfo() {
    final String username = this.generateUsername();
    int userId = this.createUser(username);
    final String adminUsername = this.generateUsername();
    this.createAdmin(adminUsername);
    String adminToken = this.authenticateAdmin(adminUsername);

    Map<String, Object> response = webClient
        .post()
        .uri(USERS_BASE_URL + "/" + userId + "/" + BALANCE_FIELD)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .header(AUTHORIZATION, "Bearer " + adminToken)
        .bodyValue(ADD_BALANCE_POST_BODY)
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(3, response.size());
    assertEquals(userId, response.get(ID_FIELD));
    assertEquals(username, response.get(USERNAME_FIELD));
    assertEquals(ADDED_BALANCE, response.get(BALANCE_FIELD));
  }

  private String generateUsername() {
    USER_COUNTER++;
    return String.format(USERNAME_TEMPLATE, USER_COUNTER);
  }

  private int createUser(String username) {
    return this.callCreateMethod(USERS_BASE_URL, username);
  }

  private String authenticateUser(String username) {
    return this.callAuthenticateMethod(USERS_BASE_URL, username);
  }

  private int createAdmin(String username) {
    return this.callCreateMethod(ADMINS_BASE_URL, username);
  }

  private String authenticateAdmin(String username) {
    return this.callAuthenticateMethod(ADMINS_BASE_URL, username);
  }

  private int callCreateMethod(String url, String username) {
    Map<String, Integer> response = webClient
        .post()
        .uri(url)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(String.format(USERNAME_AND_PWD_POST_BODY, username))
        .exchange()
        .expectStatus().isCreated()
        .expectHeader()
        .value(LOCATION_HEADER,
            startsWith("https://localhost:" + this.port + url + "/"))
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(1, response.size());
    assertTrue(response.containsKey(ID_FIELD));

    return response.get(ID_FIELD);
  }

  private String callAuthenticateMethod(String baseUrl, String username) {
    Map<String, String> response = webClient
        .post()
        .uri(baseUrl + AUTH_URL)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(String.format(USERNAME_AND_PWD_POST_BODY, username))
        .exchange()
        .expectStatus().isOk()
        .expectBody(HashMap.class)
        .returnResult()
        .getResponseBody();

    assertEquals(1, response.size());
    assertNotNull(response.get(TOKEN_FIELD));

    return response.get(TOKEN_FIELD);
  }

}
