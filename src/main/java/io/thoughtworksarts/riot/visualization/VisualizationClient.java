package io.thoughtworksarts.riot.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;


public class VisualizationClient {
    private String baseUrl = "http://127.0.0.1:5000";
    private HttpClient httpClient;

    private ObjectMapper objectMapper;


    public VisualizationClient(){
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public HttpResponse createVisualization(ArrayList<String> orderedActorIds, Map<String, Map<String, ArrayList<String>>> emotionsByActorId) {

        VisualizationDTO visualizationDTO = new VisualizationDTO(emotionsByActorId, orderedActorIds);

        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/visualization"))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(visualizationDTO)))
                    .build();
            System.out.println(objectMapper.writeValueAsString(visualizationDTO));
            return  httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }


    public void playVisualization(){

    }
}
