package io.thoughtworksarts.riot.branching;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.logger.PerceptionLogger;
import io.thoughtworksarts.riot.utilities.JSONReader;
import javafx.util.Duration;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonTranslator {

    private PerceptionLogger logger;

    public JsonTranslator(){
        this.logger = new PerceptionLogger("JsonTranslator");
    }

    public ConfigRoot populateModelsFromJson(String pathToConfig) throws Exception {
        String jsonConfig = JSONReader.readFile(pathToConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonConfig, ConfigRoot.class);
    }

    public Duration convertToDuration(String time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        long parsedTime = 0;
        try {
            parsedTime = format.parse(time).getTime();
        } catch (ParseException e) {
            logger.log(Level.INFO, "convertToDuration", e.getMessage(), null);
            e.printStackTrace();
        }
        return new Duration(parsedTime);
    }
}
