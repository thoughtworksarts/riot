package io.thoughtworksarts.riot.eyetracking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class EyeTrackingClient {

    private String message;



    public void start() {

        try {
            Socket socket = new Socket("127.0.0.1", 59253);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            String message = "";
            while(input.available() > 0) {
                byte[] bytes = new byte[1];
                bytes[0] = input.readByte();
                message += new String(bytes, StandardCharsets.US_ASCII);
            }
            System.out.println(message);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

