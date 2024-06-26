package example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_ACCEPT = "accept";
    public static final String ALL_ACCEPT = "*/*";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static Dotenv dotenv = Dotenv.load();
    public static final String BASE_URL = dotenv.get("BASE_URL");
    public static final String API_PREFIX = dotenv.get("API_PREFIX");
    public static final String ADMIN_EMAIL = dotenv.get("ADMIN_EMAIL");
    public static final String ADMIN_PASSWORD = dotenv.get("ADMIN_PASSWORD");
    public static final String USER_EMAIL = dotenv.get("USER_EMAIL");
    public static final String USER_PASSWORD = dotenv.get("USER_PASSWORD");
    public static final String ZAP_PROXY_ADDRESS = dotenv.get("ZAP_PROXY_ADDRESS");
    public static final int ZAP_PROXY_PORT = Integer.parseInt(dotenv.get("ZAP_PROXY_PORT"));
}
