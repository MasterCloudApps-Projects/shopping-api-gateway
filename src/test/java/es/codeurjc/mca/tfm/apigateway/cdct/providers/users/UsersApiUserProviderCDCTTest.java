package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ADMINS_AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;

@Provider("UsersApiUserV1Provider")
public class UsersApiUserProviderCDCTTest extends AbstractUsersApiBaseProviderCDCTTest {

  private static Integer USER_ID = null;

  private static Integer SECOND_USER_ID = null;

  private static String USER_TOKEN = null;

  private static String ADMIN_TOKEN = null;

  @State({"An user"})
  public void existentUserState() throws Exception {
    if (USER_ID == null) {
      this.createUser(VALID_CREDENTIALS_POST_BODY);
    }
  }

  @State({"A non-existent user"})
  public void nonExistentUserState() {
  }

  @State({"An authenticated user"})
  public Map<String, String> authenticatedUserState() throws Exception {
    if (USER_ID == null) {
      this.createUser(VALID_CREDENTIALS_POST_BODY);
    }

    if (USER_TOKEN == null) {
      this.authenticateUser();
    }

    return Map.of(TOKEN_FIELD, USER_TOKEN,
        ID_FIELD, String.valueOf(USER_ID)
    );
  }

  @State({"A different user authenticated"})
  public Map<String, String> authenticatedDifferentUserState() throws Exception {
    if (USER_ID == null) {
      this.createUser(VALID_CREDENTIALS_POST_BODY);
    }

    if (USER_TOKEN == null) {
      this.authenticateUser();
    }

    if (SECOND_USER_ID == null) {
      SECOND_USER_ID = this.callCreateMethod(USERS_BASE_URL, "{"
          + "  \"username\": \"alumno2@alumnos.urjc.es\","
          + "  \"password\": \"P4ssword\""
          + "}");
    }

    return Map.of(TOKEN_FIELD, USER_TOKEN,
        ID_FIELD, String.valueOf(SECOND_USER_ID)
    );

  }

  @State({"An authenticated admin"})
  public Map<String, String> authenticatedAdmin() throws Exception {
    if (ADMIN_ID == null) {
      this.createAdmin();
    }

    if (ADMIN_TOKEN == null) {
      this.authenticateAdmin();
    }

    return Map.of(TOKEN_FIELD, ADMIN_TOKEN,
        ID_FIELD, "99999999"
    );
  }

  private void createUser(String body) throws Exception {
    try {
      USER_ID = this.callCreateMethod(USERS_BASE_URL, body);
    } catch (Exception e) {
      throw new Exception("Error creating user");
    }
  }

  private void authenticateUser() throws Exception {
    try {
      USER_TOKEN = this.callAuthMethod(AUTH_URL);
    } catch (Exception e) {
      throw new Exception("Error authenticating user");
    }
  }

  private void authenticateAdmin() throws Exception {
    try {
      ADMIN_TOKEN = this.callAuthMethod(ADMINS_AUTH_URL);
    } catch (Exception e) {
      throw new Exception("Error authenticating admin");
    }
  }

  private String callAuthMethod(String path) throws Exception {
    HttpPost postMethod = new HttpPost(this.usersUrl + path);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(VALID_CREDENTIALS_POST_BODY));

    Map<String, String> responseBody;
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
        responseBody = this.objectMapper.readValue(
            httpResponse.getEntity().getContent(), HashMap.class);
      } else {
        throw new Exception("POST method auth doesn't return OK status code");
      }
    }
    return responseBody.get(TOKEN_FIELD);
  }

}
