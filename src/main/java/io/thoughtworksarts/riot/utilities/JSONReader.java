package io.thoughtworksarts.riot.utilities;

import io.thoughtworksarts.riot.logger.PerceptionLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;

public class JSONReader {
    private static PerceptionLogger logger;

    public JSONReader(){
        this.logger = new PerceptionLogger("JsonReader");
    }
    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
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
