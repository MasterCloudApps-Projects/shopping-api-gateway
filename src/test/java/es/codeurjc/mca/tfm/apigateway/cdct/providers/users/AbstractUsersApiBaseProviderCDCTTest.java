package es.codeurjc.mca.tfm.apigateway.cdct.providers.users;


import static es.codeurjc.mca.tfm.apigateway.TestConstants.ADMINS_BASE_URL;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.ID_FIELD;
import static es.codeurjc.mca.tfm.apigateway.TestConstants.VALID_CREDENTIALS_POST_BODY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.tfm.apigateway.testcontainers.TestContainersBase;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@PactFolder("target/pacts")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@Tag("ProviderCDCTTest")
public abstract class AbstractUsersApiBaseProviderCDCTTest extends TestContainersBase {

  protected static Integer ADMIN_ID = null;

  @Value("${users.url}")
  protected String usersUrl;

  protected final ObjectMapper objectMapper = new ObjectMapper();

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

  protected void createAdmin() throws Exception {
    try {
      ADMIN_ID = this.callCreateMethod(ADMINS_BASE_URL, VALID_CREDENTIALS_POST_BODY);
    } catch (Exception e) {
      throw new Exception("Error creating admin");
    }
  }

  protected Integer callCreateMethod(String path, String body) throws Exception {
    HttpPost postMethod = new HttpPost(this.usersUrl + path);
    postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    postMethod.setEntity(new StringEntity(body));

    Map<String, Integer> responseBody;
    try (CloseableHttpClient httpClient = this.getHttpClient()) {
      CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
      if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
        responseBody = this.objectMapper.readValue(
            httpResponse.getEntity().getContent(), HashMap.class);
      } else {
        throw new Exception("POST method create doesn't return CREATED status code");
      }
    }
    return responseBody.get(ID_FIELD);
  }

}
