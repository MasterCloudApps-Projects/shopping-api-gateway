package es.codeurjc.mca.tfm.apigateway.cdct;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;

public class CDCTConstants {

  public static final String ADMINS_BASE_URL = "/admins";

  public static final String ADMINS_AUTH_URL = ADMINS_BASE_URL + "/auth";

  public static final String VALID_CREDENTIALS_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020@alumnos.urjc.es\","
      + "  \"password\": \"P4ssword\""
      + "}";

  public static final String INVALID_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020\","
      + "  \"password\": \"P4ssword\""
      + "}";

  public static final String BAD_REQUEST_RESPONSE = "{"
      + "  \"error\": \"Username must be a valid email\""
      + "}";

  public static final String INVALID_CREDENTIALS_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020@alumnos.urjc.es\","
      + "  \"password\": \"Wr0ngP4ssword\""
      + "}";

  public static final String INVALID_CREDENTIALS_RESPONSE = "{"
      + "  \"error\": \"Invalid credentials.\""
      + "}";

  public static final Map<String, String> HEADERS = MapUtils.putAll(new HashMap<>(), new String[]{
      "Content-Type", APPLICATION_JSON_VALUE
  });

}
