package es.codeurjc.mca.tfm.apigateway;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Locale;
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
      + "  \"name\": \"shoes\",\n"
      + "  \"description\": \"comfortable shoes\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

  public static final String NAME_FIELD = "name";

  public static final String DESCRIPTION_FIELD = "description";

  public static final String PRICE_FIELD = "price";

  public static final String QUANTITY_FIELD = "quantity";

  public static final String INVALID_PRODUCT_POST_BODY = "{\n"
      + "  \"name\": \"sh\",\n"
      + "  \"description\": \"comfortable shoes\",\n"
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
      + "  \"name\": \"duplicated name\",\n"
      + "  \"description\": \"description\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

  public static final String PRODUCT_NOT_FOUND_RESPONSE = "{\n"
      + "  \"error\": \"Product not found\"\n"
      + "}";

  public static final String BAD_PRODUCT_ID_RESPONSE = "{"
      + "  \"error\": \"Id must be an integer\""
      + "}";

  public static final String UPDATE_PRODUCT_POST_BODY = "{\n"
      + "  \"name\": \"shoes updated\",\n"
      + "  \"description\": \"comfortable shoes updated\",\n"
      + "  \"price\": 29.99,\n"
      + "  \"quantity\": 20\n"
      + "}";

  public static final String UPDATED_PRODUCT_NAME = "shoes updated";

  public static final String UPDATED_PRODUCT_DESCRIPTION = "comfortable shoes updated";

  public static final double UPDATED_PRODUCT_PRICE = 99.12;

  public static final int UPDATED_PRODUCT_QUANTITY = 11;

  public static final String SHOPPING_CARTS_BASE_URL = "/shopping-carts";

  public static final String USER_ID_FIELD = "userId";

  public static final String COMPLETED_FIELD = "completed";

  public static final String ITEMS_FIELD = "items";

  public static final String UNIT_PRICE_FIELD = "unitPrice";

  public static final String TOTAL_PRICE_FIELD = "totalPrice";

  public static final long SHOPPING_CART_ID = 1653847319082L;

  public static final String SHOPPING_CART_ALREADY_EXISTS_MSG = "Already exists incomplete shopping cart with id=%s";

  public static final String SHOPPING_CART_ALREADY_EXISTS_RESPONSE =
      "{\"error\":\"" + SHOPPING_CART_ALREADY_EXISTS_MSG + "\"}";

  public static final String PRODUCT_ID_FIELD = "productId";

  public static final String PRODUCT_ID = "1";

  public static final String PRODUCT_PRICE_AND_QUANTITY = String.format(Locale.ENGLISH,
      "{\"unitPrice\":%.2f,\"quantity\":%d}", PRODUCT_PRICE, PRODUCT_QUANTITY);

  public static final String SHOPPING_CART_NOT_FOUND_RESPONSE = "{\n"
      + "  \"error\": \"Shopping cart not found.\"\n"
      + "}";

  public static final String CAN_NOT_DELETE_SHOPPING_CART_RESPONSE = "{\n"
      + "  \"error\": \"Can't delete completed cart\"\n"
      + "}";

  public static final String CAN_NOT_COMPLETE_SHOPPING_CART_RESPONSE = "{\n"
      + "  \"error\": \"Shopping cart can't be completed.\"\n"
      + "}";

  public static final String CAN_NOT_SET_ITEM_TO_SHOPPING_CART_RESPONSE = "{\n"
      + "  \"error\": \"Can't set item to completed cart\"\n"
      + "}";

  public static final String SET_ITEM_BAD_REQUEST_RESPONSE = "{\n"
      + "  \"error\": \"Can't set item, check item unit price and quantity to be greater than 0\"\n"
      + "}";

  public static final String CAN_NOT_DELETE_ITEM_FROM_SHOPPING_CART_RESPONSE = "{\n"
      + "  \"error\": \"Can't delete item from completed cart\"\n"
      + "}";

}
