package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.ConfigRoot;
import io.thoughtworksarts.riot.branching.model.EmotionBranch;
import io.thoughtworksarts.riot.branching.model.Intro;
import io.thoughtworksarts.riot.branching.model.Level;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class BranchingLogic {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";

    private JsonTranslator translator;
    private FacialEmotionRecognitionAPI facialRecognition;
    private Level[] levels;
    private Intro[] intros;
    @Getter private String filmPath;
    @Getter private String audioPath;

    public BranchingLogic(FacialEmotionRecognitionAPI facialRecognition, JsonTranslator translator) throws Exception {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        initialise();
    }

    private void initialise() throws Exception {
        ConfigRoot root = translator.populateModelsFromJson(PATH_TO_CONFIG);
        levels = root.getLevels();
        filmPath = root.getMedia().getVideo();
        audioPath = root.getMedia().getAudio();
        intros = root.getIntros();
    }

    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        String key = arg.getMarker().getKey();
        String[] split = key.split(":");
        String category = split[0];
        int index = Integer.parseInt(split[1]);
        Map<String, EmotionBranch> branches = levels[index - 1].getBranch();

        String seekToTime = intros[0].getStart();

        if (category.equals("level")) {
            log.info("Level Marker: " + key);
            String value = facialRecognition.getDominateEmotion().name();
            EmotionBranch emotionBranch = branches.get(value.toLowerCase());
            seekToTime = emotionBranch.getStart();
        } else if (category.equals("emotion")) {
            log.info("Emotion Marker: " + key);
            String emotionType = split[2];
            EmotionBranch emotionBranch = branches.get(emotionType);
            int outcomeNumber = emotionBranch.getOutcome();
            if (outcomeNumber > 0) {
                Level nextLevel = levels[outcomeNumber - 1];
                seekToTime = nextLevel.getStart();
            } else {
                log.info("Credits");
                seekToTime = "11:05.000";
            }
        }
        else if (category.equals("intro")) {
            log.info("Intro slide: " + key);
            seekToTime = "00:00.000";
        }

        //not sure what to do here but something horrible went wrong!
        log.info("Seeking: " + seekToTime);
        return translator.convertToDuration(seekToTime);
    }

    public void recordMarkers(Map<String, Duration> markers) {
        markers.put("intro:3", translator.convertToDuration(intros[2].getEnd()));
        for (Level level : levels) {
            markers.put("level:" + level.getLevel(), translator.convertToDuration(level.getEnd()));
            Map<String, EmotionBranch> branch = level.getBranch();
            branch.forEach((branchKey, emotionBranch) -> markers.put(
                    "emotion:" + level.getLevel() + ":" + branchKey,
                    translator.convertToDuration(emotionBranch.getEnd())));
        }
    }

    public Duration getProperIntroDuration(Duration currentTime) {
        Duration beginningOfIntro = translator.convertToDuration(intros[0].getStart());
        Duration secondIntroStart = translator.convertToDuration(intros[1].getStart());
        Duration thirdIntroStart = translator.convertToDuration(intros[2].getStart());
        Duration endOfIntro = translator.convertToDuration(intros[2].getEnd());

        Duration[] durations = new Duration[]{secondIntroStart, thirdIntroStart, endOfIntro};

        for (Duration duration : durations) {
            if (currentTime.lessThan(duration) && currentTime.greaterThan(beginningOfIntro)) {
                return duration;
            }
        }
        return null;
    }
}
