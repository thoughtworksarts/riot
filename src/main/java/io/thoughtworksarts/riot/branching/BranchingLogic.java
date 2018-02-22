package io.thoughtworksarts.riot.branching;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thoughtworksarts.riot.utilities.JSONReader;
import javafx.collections.ObservableMap;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class BranchingLogic {

    @Getter private ConfigRoot root;

    public ConfigRoot createLogicTree(String pathToConfig) throws Exception {
        String jsonConfig = JSONReader.readFile(pathToConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        root = objectMapper.readValue(jsonConfig, ConfigRoot.class);
        return root;
    }

    public Level getOutcome(EmotionBranch currentEmotionBranch) {
        int outcome = currentEmotionBranch.getOutcome();
        if ( outcome == 0 ) return null;
        return root.getLevels()[outcome - 1];
    }

    public void recordMarkers(ObservableMap<String, Duration> markers) {
        Level[] levels = root.getLevels();
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        for(Level level: root.getLevels()) {
            markers.put("level:" + level.getLevel(), stringToDuration(level.getEnd()));

            Map<String, EmotionBranch> branch = level.getBranch();
            branch.forEach((branchKey, emotionBranch) -> {
                markers.put("emotion:" + level.getLevel() +":"+ branchKey, stringToDuration(emotionBranch.getEnd()));
            });
        }
    }

    public Duration stringToDuration(String time) {
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
