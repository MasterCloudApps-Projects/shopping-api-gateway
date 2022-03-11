package es.codeurjc.mca.tfm.apigateway;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;

public class TestConstants {

  public static final String AUTH_URL = "/auth";

  public static final String ADMINS_BASE_URL = "/admins";

  public static final String ADMINS_AUTH_URL = ADMINS_BASE_URL + AUTH_URL;

  public static final String USERS_BASE_URL = "/users";

  public static final String VALID_CREDENTIALS_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020@alumnos.urjc.es\","
      + "  \"password\": \"P4ssword\""
      + "}";

  public static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZSI6IkFETUlOX1JPTEUiLCJpYXQiOjE2MzczNDY5ODEsImV4cCI6MTYzNzM0NzI4MX0.3s7zdOKbrY2CTMfd4qkQbapLMId-DlQL55Il05wWAFA";

  public static final String JWT_TOKEN_RESPONSE = "{"
      + "\"token\":\"" + JWT_TOKEN + "\""
      + "}";

  public static final String INVALID_USER_POST_BODY = "{"
      + "  \"username\": \"a.martinmar.2020\","
      + "  \"password\": \"P4ssword\""
      + "}";

  public static final String USERNAME_BAD_REQUEST_RESPONSE = "{"
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

  public static final String USERNAME = "a.martinmar.2021@alumnos.urjc.es";

  public static final double BALANCE = 0.0;

  public static final String CREATED_RESPONSE = "{"
      + "\"id\":1"
      + "}";

  public static final String ADMIN_ALREADY_EXISTS_RESPONSE = "{\n"
      + "    \"error\": \"Already exists an admin with that username\"\n"
      + "  }";

  public static final String USER_ALREADY_EXISTS_RESPONSE = "{\n"
      + "    \"error\": \"Already exists an user with that username\"\n"
      + "  }";

  public static final String ID_FIELD = "id";

  public static final String USERNAME_FIELD = "username";

  public static final String BALANCE_FIELD = "balance";

  public static final String TOKEN_FIELD = "token";

  public static final String MISSING_TOKEN_RESPONSE = "{\n"
      + "  \"error\": \"No token provided.\"\n"
      + "}";

  public static final String NOT_ALLOWED_RESPONSE = "{\n"
      + "  \"error\": \"You don't have permission to access the resource\"\n"
      + "}";

  public static final String USER_NOT_FOUND_RESPONSE = "{\n"
      + "  \"error\": \"User not found\"\n"
      + "}";

  public static final String BEARER_PREFIX = "Bearer ";

  public static final String BEARER_TOKEN = BEARER_PREFIX + JWT_TOKEN;

  public static final String INVALID_BEARER_TOKEN = BEARER_PREFIX
      + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwicm9sZSI6IkFETUlOX1JPTEUiLCJpYXQiOjE2MzczNDY5ODEsImV4cCI6MTYzNzM0NzI4MX0.CatmBe4laGqFzAGyn4-OXB3JojARpVlHAGmufrowv1Q";

  public static final String INVALID_TOKEN_RESPONSE = "{\n"
      + "  \"error\": \"Invalid or expired token.\"\n"
      + "}";

  public static final double ADDED_BALANCE = 35.8;

  public static final String ADD_BALANCE_POST_BODY = "{\n"
      + "  \"amount\": " + ADDED_BALANCE + "\n"
      + "}";

  public static final String INVALID_ADD_BALANCE_POST_BODY = "{\n"
      + "  \"amount\": 0\n"
      + "}";

  public static final String ADD_BALANCE_BAD_REQUEST_RESPONSE = "{\n"
      + "  \"error\": \"Amount to add must be greater than 0\"\n"
      + "}";

  public static final Map<String, String> HEADERS = MapUtils.putAll(new HashMap<>(), new String[]{
      CONTENT_TYPE, APPLICATION_JSON_VALUE
  });

  public static final String LOCATION_HEADER = "Location";

  public static final String PRODUCTS_BASE_URL = "/products";

  public static final String PRODUCT_NAME = "shoes";

  public static final String PRODUCT_DESCRIPTION = "comfortable shoes";

  public static final double PRODUCT_PRICE = 29.99;

  public static final int PRODUCT_QUANTITY = 20;

  public static final String VALID_PRODUCT_POST_BODY = "{\n"
      + "  \"name\": \"SHOES\",\n"
      + "  \"description\": \"COMFORTABLE SHOES\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

  public static final String NAME_FIELD = "name";

  public static final String DESCRIPTION_FIELD = "description";

  public static final String PRICE_FIELD = "price";

  public static final String QUANTITY_FIELD = "quantity";

  public static final String INVALID_PRODUCT_POST_BODY = "{\n"
      + "  \"name\": \"SH\",\n"
      + "  \"description\": \"COMFORTABLE SHOES\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

  public static final String PRODUCT_BAD_REQUEST_RESPONSE = "{"
      + "  \"error\": \"Name is mandatory and must have a minimum length of 3\""
      + "}";

  public static final String PRODUCT_ALREADY_EXISTS_RESPONSE = "{\n"
      + "    \"error\": \"Already exists a product with that name\"\n"
      + "  }";

  public static final String ALREADY_EXISTENT_NAME_PRODUCT_POST_BODY = "{\n"
      + "  \"name\": \"DUPLICATED NAME\",\n"
      + "  \"description\": \"DESCRIPTION\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

}
