package io.thoughtworksarts.riot.eyetracking;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.thoughtworksarts.riot.video.MediaControl;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class EyeTrackingClient {

    private ObjectMapper objectMapper;

    private Process p;

    private static final String PATH_TO_CALIBRATION_SCRIPT = "C:/Users/Kiosk/perception-calibration/app.py";

    private MediaControl mediaControl;

    public EyeTrackingClient(MediaControl mediaControl) {

        objectMapper = new ObjectMapper();
        this.mediaControl = mediaControl;
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

    public boolean isAlive(){
        return p.isAlive();
    }

    public void calibrate() {
        mediaControl.pause();
        new Thread(() -> {
            log.info("Beginning calibration...");
            try {
                log.info("Attempting calibration...");
                ProcessBuilder pb = new ProcessBuilder();

                pb.command("python", PATH_TO_CALIBRATION_SCRIPT, "--simulate-success");
                log.info("Created Command");
                p = pb.start();
                log.info("Started Process");
                //ProcessBuilder pb = new ProcessBuilder("python", PATH_TO_CALIBRATION_SCRIPT, "--simulate-success");
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                log.info("Created BufferReaders");
                String line;
                while ((line = in.readLine()) != null) {
                    log.info(line);
                }
                while ((line = errorReader.readLine()) != null) {
                    log.info(line);
                }
                int exitCode = p.waitFor();
                log.info("Waited for Process");
                log.info("Exited with error code: " + exitCode );
            } catch (IOException |
                    InterruptedException e) {
                log.info("Calibration failed.");
                e.printStackTrace();
            }
            finally{
                    mediaControl.play();
            }



        }).start();

    }


}

