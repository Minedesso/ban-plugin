package de.minedesso.banPlugin.api;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReasonApiService extends BaseApiService {

    private static ReasonApiService instance;

    public static ReasonApiService getInstance() {
        if (instance == null) {
            instance = new ReasonApiService();
        }
        return instance;
    }

    private ReasonApiService() {
        super();
    }

    /**
     * Validates if a reason ID exists in the API.
     * @param reasonId the ID of the reason to validate
     * @return true if the reason ID is valid, false otherwise
     */
    public boolean isReasonValid(long reasonId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/reason/validate/" + reasonId))
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

}
