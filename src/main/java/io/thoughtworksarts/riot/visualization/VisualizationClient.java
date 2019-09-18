package io.thoughtworksarts.riot.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class VisualizationClient {
    private String baseUrl = "http://127.0.0.1:5000";
    private HttpClient httpClient;

    private ObjectMapper objectMapper;


    public VisualizationClient(){
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public CompletableFuture<HttpResponse<String>> createVisualization(String actorId, ArrayList dominantEmotions, ArrayList scenesPlayed) {
        log.info("Creating visualization");
        VisualizationDTO visualizationDTO = new VisualizationDTO(actorId, dominantEmotions, scenesPlayed);

        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/visualization"))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(visualizationDTO)))
                    .build();

            System.out.println(objectMapper.writeValueAsString(visualizationDTO));
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }
}
