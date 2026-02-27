package de.minedesso.banPlugin.api;

import de.minedesso.banPlugin.api.out.BanDto;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BanApiService extends BaseApiService {

    private static BanApiService instance;

    public static BanApiService getInstance() {
        if (instance == null) {
            instance = new BanApiService();
        }
        return instance;
    }

    private BanApiService() {
        super();
    }

    /**
     * Checks if a player is already banned.
     * @param playerName the name of the player to check
     * @return true if the player is banned, false otherwise
     */
    public boolean isPlayerBanned(String playerName) {
        try {
            String encoded = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
            // URLEncoder encodes spaces as '+', replace with %20 for path segments
            encoded = encoded.replace("+", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/ban/validate?name=" + encoded))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException ie) {
            // Restore interrupt status and return false as fallback
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a player is already banned by UUID.
     * @param uuid the UUID of the player to check
     * @return true if the player is banned, false otherwise
     */
    public boolean isPlayerBanned(UUID uuid) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/ban/validate/" + uuid.toString()))
                    .GET()
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (InterruptedException ie) {
            // Restore interrupt status and return false as fallback
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a new ban for a player.
     * @param banDto the ban data to send to the API
     * @return true if the ban was created successfully, false otherwise
     */
    public boolean createBan(BanDto banDto) {
        try {
            String json = objectMapper.writeValueAsString(banDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/ban"))
                    .header(HEADER_CONTENT_TYPE, APPLICATION_JSON)
                    .header(HEADER_ACCEPT, APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            return status == 200 || status == 201;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}


