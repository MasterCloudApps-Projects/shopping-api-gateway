package es.codeurjc.mca.tfm.apigateway.cdct.consumers.users;

import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.ADMINS_AUTH_URL;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.BAD_REQUEST_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.HEADERS;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_CREDENTIALS_RESPONSE;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.INVALID_POST_BODY;
import static es.codeurjc.mca.tfm.apigateway.cdct.CDCTConstants.VALID_CREDENTIALS_POST_BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

@ExtendWith({PactConsumerTestExt.class})
@PactTestFor(providerName = "UsersApiAdminV1Provider", pactVersion = PactSpecVersion.V3)
public class UsersApiAdminConsumerCDCTTest {

  @BeforeEach
  public void setUp(MockServer mockServer) {
    assertNotNull(mockServer);
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthentication(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with valid credentials")
        .path(ADMINS_AUTH_URL)
        .method("POST")
        .headers(HEADERS)
        .body(VALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.OK.value())
        .body(new PactDslJsonBody()
            .stringType("token"))
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidBody(PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with wrong body")
        .method("POST")
        .headers(HEADERS)
        .body(INVALID_POST_BODY)
        .path(ADMINS_AUTH_URL)
        .willRespondWith()
        .status(HttpStatus.BAD_REQUEST.value())
        .body(BAD_REQUEST_RESPONSE)
        .toPact();
  }

  @Pact(consumer = "UsersApiAdminV1Consumer")
  public RequestResponsePact adminAuthenticationWithInvalidCredentials(
      PactDslWithProvider builder) {

    return builder
        .given("An administrator")
        .uponReceiving("authenticating with invalid credentials")
        .path(ADMINS_AUTH_URL)
        .method("POST")
        .headers(HEADERS)
        .body(INVALID_CREDENTIALS_POST_BODY)
        .willRespondWith()
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(INVALID_CREDENTIALS_RESPONSE)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "adminAuthentication")
  void testAdminAuthentication(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(VALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.OK.value(), httpResponse.getCode());
    assertNotNull(IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidBody")
  void testAdminAuthenticationWithInvalidBody(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.BAD_REQUEST.value(), httpResponse.getCode());
    assertEquals(BAD_REQUEST_RESPONSE, IOUtils.toString(httpResponse.getEntity().getContent()));

  }

  @Test
  @PactTestFor(pactMethod = "adminAuthenticationWithInvalidCredentials")
  void testAdminAuthenticationWithInvalidCredentials(MockServer mockServer) throws IOException {

    // when
    ClassicHttpResponse httpResponse = (ClassicHttpResponse) Request
        .post(mockServer.getUrl() + ADMINS_AUTH_URL)
        .setHeader("Content-Type", APPLICATION_JSON_VALUE)
        .bodyString(INVALID_CREDENTIALS_POST_BODY, ContentType.APPLICATION_JSON)
        .execute()
        .returnResponse();

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), httpResponse.getCode());
    assertEquals(INVALID_CREDENTIALS_RESPONSE,
        IOUtils.toString(httpResponse.getEntity().getContent()));

  }

}
