package io.thoughtworksarts.riot.utilities;

import io.thoughtworksarts.riot.logger.PerceptionLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class JSONReader {
    private static PerceptionLogger logger;

    public JSONReader(){
        this.logger = new PerceptionLogger("JsonReader");
    }
    public static String readFile(String filename) {
        String result = "";
        try {
            final InputStream resourceAsStream = JSONReader.class.getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            logger.log(Level.INFO, "readFile", e.getMessage(), null);
            e.printStackTrace();
        }
        return result;
    }
}
