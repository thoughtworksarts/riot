package io.thoughtworksarts.riot.eyetracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class EyeTrackingClient {

    private ObjectMapper objectMapper;

    private static final String PATH_TO_CALIBRATION_SCRIPT = "C:/Users/laure/repos/perception-calibration/app.py";

    public EyeTrackingClient() {
         objectMapper = new ObjectMapper();
    }

    public void startEyeTracking() {
        log.info("Starting eye tracking");
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
        log.info("Stopping eye tracking");
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

    public void calibrate() {
        new Thread(() -> {
            log.info("Beginning calibration...");
            try {
                log.info("Attempting calibration...");
                ProcessBuilder pb = new ProcessBuilder("python", PATH_TO_CALIBRATION_SCRIPT, "--simulate-success");
                Process p = pb.start();
                p.waitFor();
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    try {
                        if (line.equals("Calibration process concluded")) {
                            log.info(line);
                        }
                    } catch (Exception e) {
                        log.info("Failure during calibration.");
                        e.printStackTrace();
                    }
                }
            } catch (IOException |
                    InterruptedException e) {
                log.info("Calibration failed.");
                e.printStackTrace();
            }

        });//.start();
    }


}

