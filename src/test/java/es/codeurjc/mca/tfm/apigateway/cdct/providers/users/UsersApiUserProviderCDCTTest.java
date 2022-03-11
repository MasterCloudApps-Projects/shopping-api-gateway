package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.TOKEN_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_CREDENTIALS_POST_BODY;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.AbstractBaseProviderCDCTTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;

@Provider("UsersApiUserV1Provider")
@DisplayName("Users API user resources provider CDCT tests")
public class UsersApiUserProviderCDCTTest extends AbstractBaseProviderCDCTTest {

  private static Integer SECOND_USER_ID = null;

  @Override
  protected String getUrl() {
    return this.usersUrl;
  }

  @State({"A non-existent user"})
  public void nonExistentUserState() {
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
      SECOND_USER_ID = this.callCreateMethod(this.usersUrl + USERS_BASE_URL, "{"
          + "  \"username\": \"alumno2@alumnos.urjc.es\","
          + "  \"password\": \"P4ssword\""
          + "}");
    }

    return Map.of(TOKEN_FIELD, USER_TOKEN,
        ID_FIELD, String.valueOf(SECOND_USER_ID)
    );

  }

}
