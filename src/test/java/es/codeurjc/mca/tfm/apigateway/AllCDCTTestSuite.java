package es.codeurjc.mca.tfm.apigateway;

import es.codeurjc.mca.tfm.apigateway.cdct.consumers.users.UsersApiAdminConsumerCDCTTest;
import es.codeurjc.mca.tfm.apigateway.cdct.consumers.users.UsersApiUsersConsumerCDCTTest;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.users.UsersApiAdminProviderCDCTTest;
import es.codeurjc.mca.tfm.apigateway.cdct.providers.users.UsersApiUserProviderCDCTTest;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("CDCT Test suite")
@SelectClasses({
    UsersApiAdminConsumerCDCTTest.class,
    UsersApiUsersConsumerCDCTTest.class,
    UsersApiAdminProviderCDCTTest.class,
    UsersApiUserProviderCDCTTest.class
})
@IncludeTags({"ConsumerCDCTTest", "ProviderCDCTTest"})
public class AllCDCTTestSuite {

}
