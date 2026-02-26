package de.minedesso.banPlugin.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * BaseApiService is an abstract parent class for all API service classes.
 * Provides shared attributes and initialization logic for HTTP communication.
 */
public abstract class BaseApiService {

    protected static final String HEADER_ACCEPT = "Accept";
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json";

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final String apiUrl;

    /**
     * Initializes the BaseApiService with HTTP client, object mapper, and API URL.
     * Environment variable: API_URL (default: http://localhost:8080/api)
     */
    protected BaseApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        String env = System.getenv("API_URL");
        this.apiUrl = env != null && !env.isBlank() ? env : "http://localhost:8080/api";
    }
}

