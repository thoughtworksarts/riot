package io.thoughtworksarts.riot.branching;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Slf4j
public class JsonTranslator {

    @Getter private ConfigRoot root;

    public ConfigRoot populateModelsFromJson(String pathToConfig) throws Exception {
        String jsonConfig = readFile(pathToConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        root = objectMapper.readValue(jsonConfig, ConfigRoot.class);
        return root;
    }

    private String readFile(String filename) {
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
            e.printStackTrace();
        }
        return result;
    }

    public Duration convertToDuration(String time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        long parsedTime = 0;
        try {
            parsedTime = format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Duration(parsedTime);
    }
}
