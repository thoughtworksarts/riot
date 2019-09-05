package io.thoughtworksarts.riot.eyetracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thoughtworksarts.riot.visualization.VisualizationDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class EyeTrackingClient {

    private ObjectMapper objectMapper;

    public EyeTrackingClient() {

    }

    public void startEyeTracking() {
        String url = "http://127.0.0.1:5000/eye-tracking/start";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            if(con.getResponseCode() != 200)
                throw new RuntimeException();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopEyeTracking() {
        String url = "http://127.0.0.1:5000/eye-tracking/stop";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            if(con.getResponseCode() != 200)
                throw new RuntimeException();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void setRequestBody(Object body, HttpURLConnection con) throws IOException {
        con.setDoOutput(true);
        con.addRequestProperty("Content-Type", "application/json");
        String query = objectMapper.writeValueAsString(body);
        con.setRequestProperty("Content-Length", Integer.toString(query.length()));
        con.getOutputStream().write(query.getBytes("UTF8"));
    }
}

