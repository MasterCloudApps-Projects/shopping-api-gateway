package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

@Provider("UsersApiAdminV1Provider")
public class UsersApiAdminProviderCDCTTest extends AbstractUsersApiBaseProviderCDCTTest {

  @State({"An administrator"})
  public void adminAuthenticationState() throws Exception {

    HttpPost postMethod = new HttpPost(this.usersUrl + ADMINS_BASE_URL);
    postMethod.setHeader("Content-Type", APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(VALID_CREDENTIALS_POST_BODY));

    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      httpClient.execute(postMethod);
    }
  }

  @State({"A non-existent admin"})
  public void nonExistentAdmin() {
  }

}
