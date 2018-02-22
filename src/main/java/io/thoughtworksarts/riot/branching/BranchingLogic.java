package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.branching.model.EmotionBranch;
import io.thoughtworksarts.riot.branching.model.Level;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
import javafx.collections.ObservableMap;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class BranchingLogic {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";

    private JsonTranslator translator;
    private DummyFacialRecognitionAPI facialRecognition;
    private Level[] levels;
    @Getter private String filmPath;
    @Getter private String audioPath;

    public BranchingLogic(DummyFacialRecognitionAPI facialRecognition, JsonTranslator translator) throws Exception {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        ConfigRoot root = translator.populateModelsFromJson(PATH_TO_CONFIG);
        levels = root.getLevels();
        filmPath = root.getMedia().getVideo();
        audioPath = root.getMedia().getAudio();
    }

    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        String key = arg.getMarker().getKey();
        String[] split = key.split(":");
        String category = split[0];
        int index = Integer.parseInt(split[1]);
        Level level = levels[index - 1];

        String seekToTime = "00:00.000";

        if (category.equals("level")) {
            log.info("Level Marker: " + key);
            String value = facialRecognition.getDominateEmotion().name();
            EmotionBranch emotionBranch = level.getBranch().get(value.toLowerCase());
            seekToTime = emotionBranch.getStart();
        } else if (category.equals("emotion")) {
            log.info("Emotion Marker: " + key);
            String emotionType = split[2];
            EmotionBranch emotionBranch = level.getBranch().get(emotionType);
            int outcomeNumber = emotionBranch.getOutcome();
            if (outcomeNumber > 0) {
                Level nextLevel = levels[outcomeNumber - 1];
                seekToTime = nextLevel.getStart();
            }
        }
        //not sure what to do here but something horrible went wrong!
        log.info("Seeking: " + seekToTime);
        return translator.convertToDuration(seekToTime);
    }

    public void recordMarkers(ObservableMap<String, Duration> markers) {
        for (Level level : levels) {
            markers.put("level:" + level.getLevel(), translator.convertToDuration(level.getEnd()));
            Map<String, EmotionBranch> branch = level.getBranch();
            branch.forEach((branchKey, emotionBranch) -> {
                markers.put("emotion:" + level.getLevel() + ":" + branchKey, translator.convertToDuration(emotionBranch.getEnd()));
            });
        }
    }
}
