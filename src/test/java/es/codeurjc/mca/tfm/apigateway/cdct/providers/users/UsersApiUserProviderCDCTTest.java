package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.USERS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import es.codeurjc.mca.tfm.apigateway.testcontainers.AbstractContainerBaseTest;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Provider("UsersApiUserV1Provider")
@PactFolder("target/pacts")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
public class UsersApiUserProviderCDCTTest extends AbstractContainerBaseTest {

  @Value("${users.url}")
  private String usersUrl;

  @BeforeEach
  void before(PactVerificationContext context) {
    URI backendUri = URI.create(this.usersUrl);
    context.setTarget(
        new HttpsTestTarget(backendUri.getHost(), backendUri.getPort(), backendUri.getPath(),
            true));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State({"An user"})
  public void userAuthenticationState() throws Exception {

    HttpPost postMethod = new HttpPost(this.usersUrl + USERS_BASE_URL);
    postMethod.setHeader("Content-Type", APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(VALID_CREDENTIALS_POST_BODY));

    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      httpClient.execute(postMethod);
    }
  }

  @State({"A non-existent user"})
  public void nonExistentAdmin() throws Exception {
  }

  private CloseableHttpClient getHttpClient()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    final TrustStrategy acceptingTrustStrategy = (certificate, authType) -> true;

    final SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(null, acceptingTrustStrategy)
        .build();

    final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
        NoopHostnameVerifier.INSTANCE);
    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("https", sslsf).build();
    PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(
        socketFactoryRegistry);

    return HttpClients.custom()
        .setSSLSocketFactory(sslsf)
        .setConnectionManager(clientConnectionManager)
        .build();

  }

}
