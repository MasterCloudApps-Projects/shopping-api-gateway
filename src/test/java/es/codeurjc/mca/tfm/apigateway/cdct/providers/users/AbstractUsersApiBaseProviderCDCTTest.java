package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;


import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import java.io.File;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
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
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@ExtendWith(SpringExtension.class)
@PactFolder("target/pacts")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
public abstract class AbstractUsersApiBaseProviderCDCTTest {

  public static final String MYSQL_SERVICE_NAME = "mysql_1";

  public static final int MYSQL_PORT = 3306;

  public static final String USERS_SERVICE_NAME = "users_1";

  public static final int USERS_PORT = 8443;

  protected static final DockerComposeContainer ENVIRONMENT;

  static {
    ENVIRONMENT =
        new DockerComposeContainer(new File("src/test/resources/users-docker-compose-test.yml"))
            .withExposedService(MYSQL_SERVICE_NAME, MYSQL_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService(USERS_SERVICE_NAME, USERS_PORT,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
    ENVIRONMENT.start();
  }

  @Value("${users.url}")
  protected String usersUrl;

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

  protected CloseableHttpClient getHttpClient()
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
