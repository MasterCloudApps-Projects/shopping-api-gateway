package es.codeurjc.mca.tfm.apigateway.integration.users;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADDED_BALANCE;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADD_BALANCE_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BALANCE_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.BEARER_PREFIX;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERNAME_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERS_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        .header(AUTHORIZATION, BEARER_PREFIX + userToken)
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
        .header(AUTHORIZATION, BEARER_PREFIX + userToken)
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
        .header(AUTHORIZATION, BEARER_PREFIX + adminToken)
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
        .header(AUTHORIZATION, BEARER_PREFIX + adminToken)
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

}
