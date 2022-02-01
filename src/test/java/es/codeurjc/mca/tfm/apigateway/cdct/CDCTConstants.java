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

  public static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZSI6IkFETUlOX1JPTEUiLCJpYXQiOjE2MzczNDY5ODEsImV4cCI6MTYzNzM0NzI4MX0.3s7zdOKbrY2CTMfd4qkQbapLMId-DlQL55Il05wWAFA";

  public static final String JWT_TOKEN_RESPONSE = "{"
      + "\"token\":\""+JWT_TOKEN+"\""
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

  public static final String VALID_USERNAME_AND_PWD_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2021@alumnos.urjc.es\","
      + "  \"password\": \"P4ssword\""
      + "}";

  public static final int ID = 1;

  public static final String CREATED_RESPONSE = "{"
      + "\"id\":1"
      + "}";

  public static final String ADMIN_ALREADY_EXISTS_RESPONSE = "{\n"
      + "    \"error\": \"Already exists an admin with that username\"\n"
      + "  }";


  public static final Map<String, String> HEADERS = MapUtils.putAll(new HashMap<>(), new String[]{
      "Content-Type", APPLICATION_JSON_VALUE
  });

  public static final String LOCATION_HEADER = "Location";


}
