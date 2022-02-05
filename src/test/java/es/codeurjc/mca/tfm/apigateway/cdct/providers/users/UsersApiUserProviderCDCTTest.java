package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ADMINS_AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;

@Provider("UsersApiUserV1Provider")
public class UsersApiUserProviderCDCTTest extends AbstractUsersApiBaseProviderCDCTTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @State({"An user"})
  public void existentUserState() throws Exception {
    this.createUser(VALID_CREDENTIALS_POST_BODY);
  }

  @State({"A non-existent user"})
  public void nonExistentUserState() {
  }

  @State({"An authenticated user"})
  public Map<String, String> authenticatedUserState() throws Exception {
    Integer createdUserId = this.createUser(VALID_CREDENTIALS_POST_BODY);
    if (createdUserId == null) {
      throw new Exception("Error creating user");
    }

    HttpPost postMethod = new HttpPost(this.usersUrl + AUTH_URL);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(VALID_CREDENTIALS_POST_BODY));

    Map<String, String> responseBody;
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      responseBody = new ObjectMapper().readValue(
          httpResponse.getEntity().getContent(), HashMap.class);
    }
    responseBody.put(ID_FIELD, String.valueOf(createdUserId));
    return responseBody;

  }

  @State({"A different user authenticated"})
  public Map<String, String> authenticatedDifferentUserState() throws Exception {
    final String firstUserPostBody = "{"
        + "  \"username\": \"alumno1@alumnos.urjc.es\","
        + "  \"password\": \"P4ssword\""
        + "}";
    Integer firstUserCreatedId = this.createUser(firstUserPostBody);
    if (firstUserCreatedId == null) {
      throw new Exception("Error creating first user");
    }

    Integer secondUserCreatedId = this.createUser("{"
        + "  \"username\": \"alumno2@alumnos.urjc.es\","
        + "  \"password\": \"P4ssword\""
        + "}");
    if (secondUserCreatedId == null) {
      throw new Exception("Error creating second user");
    }

    HttpPost postMethod = new HttpPost(this.usersUrl + AUTH_URL);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(firstUserPostBody));

    Map<String, String> responseBody;
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      responseBody = new ObjectMapper().readValue(
          httpResponse.getEntity().getContent(), HashMap.class);
    }
    responseBody.put(ID_FIELD, String.valueOf(secondUserCreatedId));
    return responseBody;

  }

  @State({"An authenticated admin"})
  public Map<String, String> authenticatedAdmin() throws Exception {
    Integer createdUserId = this.createAdmin();
    if (createdUserId == null) {
      throw new Exception("Error creating admin");
    }

    HttpPost postMethod = new HttpPost(this.usersUrl + ADMINS_AUTH_URL);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(VALID_CREDENTIALS_POST_BODY));

    Map<String, String> responseBody;
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      responseBody = new ObjectMapper().readValue(
          httpResponse.getEntity().getContent(), HashMap.class);
    }
    responseBody.put(ID_FIELD, String.valueOf(99999999));
    return responseBody;
  }

  private Integer createUser(String body) throws Exception {
    HttpPost postMethod = new HttpPost(this.usersUrl + USERS_BASE_URL);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(body));

    Map<String, Integer> responseBody = Map.of();
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
        responseBody = new ObjectMapper().readValue(
            httpResponse.getEntity().getContent(), HashMap.class);
      }
    }
    return responseBody.get(ID_FIELD);
  }

  private Integer createAdmin() throws Exception {
    HttpPost postMethod = new HttpPost(this.usersUrl + ADMINS_BASE_URL);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity("{"
        + "  \"username\": \"newAdmin@alumnos.urjc.es\","
        + "  \"password\": \"P4ssword\""
        + "}"));

    Map<String, Integer> responseBody = Map.of();
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
        responseBody = new ObjectMapper().readValue(
            httpResponse.getEntity().getContent(), HashMap.class);
      }
    }
    return responseBody.get(ID_FIELD);
  }

}
