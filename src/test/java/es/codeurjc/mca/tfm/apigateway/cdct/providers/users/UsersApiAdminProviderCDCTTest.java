package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;

@Provider("UsersApiAdminV1Provider")
public class UsersApiAdminProviderCDCTTest extends AbstractUsersApiBaseProviderCDCTTest {

  @State({"An administrator"})
  public void existentAdminState() throws Exception {
    if (ADMIN_ID == null) {
      this.createAdmin();
    }
  }

  @State({"A non-existent admin"})
  public void nonExistentAdminState() {
  }

}
