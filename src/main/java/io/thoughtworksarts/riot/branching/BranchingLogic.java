package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.eyetracking.EyeTrackingClient;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BranchingLogic {

    private JsonTranslator translator;
    private FacialEmotionRecognitionAPI facialRecognition;
    private Level[] levels;
    private Intro[] intros;
    private Map<String, ArrayList<String>> emotionsByActorId;
    private Credits[] credits;
    private String actorId;
    private boolean visitedIntro = false;
    private EyeTrackingClient eyeTrackingClient;


    public BranchingLogic(FacialEmotionRecognitionAPI facialRecognition, JsonTranslator translator, ConfigRoot configRoot) {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        this.levels = configRoot.getLevels();
        this.intros = configRoot.getIntros();
        this.credits = configRoot.getCredits();
        this.emotionsByActorId = new HashMap<>();
        this.actorId = "1";
        this.eyeTrackingClient = new EyeTrackingClient();

    }

    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        String key = arg.getMarker().getKey();
        String[] split = key.split(":");
        String category = split[0];
        int index = Integer.parseInt(split[1]);
        Map<String, EmotionBranch> branches = levels[index - 1].getBranch();

        switch (category) {
            case "level": {
                log.info("Level Marker: " + key);
                String value = facialRecognition.getDominantEmotion().name();
                EmotionBranch emotionBranch = branches.get(value.toLowerCase());
                return translator.convertToDuration(emotionBranch.getStart());
            }
            case "level start": {
                log.info("Start eye tracking");
                if (index == 1) {
                    this.eyeTrackingClient.startEyeTracking();
                }
            }
            case "emotion": {
                log.info("Emotion Marker: " + key);
                String emotionType = split[2];

                if (!emotionsByActorId.containsKey(actorId)) {
                    emotionsByActorId.put(actorId, new ArrayList<>());
                }
                emotionsByActorId.get(actorId).add(emotionType);
                EmotionBranch emotionBranch = branches.get(emotionType);
                int outcomeNumber = emotionBranch.getOutcome();
                log.info("Stopping eye tracking");

                if (outcomeNumber > 0) {
                    Level nextLevel = levels[outcomeNumber - 1];

                    return translator.convertToDuration(nextLevel.getStart());
                } else {
                    this.eyeTrackingClient.stopEyeTracking();
                    log.info("Credits: ");
                    //TODO: make request to send over emotions to python application
                    facialRecognition.endImageCapture();
                    return translator.convertToDuration(credits[0].getStart());
                }
            }
            case "intro": {
                log.info("Intro slide: " + key);
                return translator.convertToDuration("00:00.000");
            }
            case "credit": {
                if (split[1].equals("2")) {

                    log.info("Shutting down webcam: ");

                    ArrayList<String> orderedActorIds = new ArrayList<>();
                    orderedActorIds.add(actorId);
                    eyeTrackingClient.createEyeTrackingVisualization(orderedActorIds, emotionsByActorId);

                    facialRecognition.endImageCapture();
                    log.info("Exiting application: ");

//                Platform.exit();
                }
            }
        }
        double currentTime = arg.getMarker().getValue().toMillis() + 1;
        return new Duration(currentTime);
    }

    public void addMarker(Map<String, Duration> markers, String nameForMarker, String index, String time) {
        String markerNameWithColon = nameForMarker + ":" + index;
        markers.put(markerNameWithColon, translator.convertToDuration(time));
    }

    public void recordMarkers(Map<String, Duration> markers) {
        Integer indexOfLastIntro = intros.length - 1;
        Integer indexOfLastCredits = credits.length - 1;
        addMarker(markers, "intro", indexOfLastIntro.toString(),
                intros[indexOfLastIntro].getEnd());
        addMarker(markers, "credit", indexOfLastCredits.toString(),
                credits[indexOfLastCredits].getEnd());

        for (Level level : levels) {
            addMarker(markers, "level start", String.valueOf(level.getLevel()), level.getStart());
            addMarker(markers, "level", String.valueOf(level.getLevel()), level.getEnd());
            Map<String, EmotionBranch> branch = level.getBranch();
            branch.forEach(
                    (branchKey, emotionBranch) -> addMarker(markers, "emotion:" + level.getLevel(),
                    branchKey, emotionBranch.getEnd()));
        }
    }

    public Duration getClickSeekTime(Duration currentTime) {

        HashMap<String, Duration> durations = new HashMap<>();

        //TODO: Refactor - maybe this could be one method that takes a list
        addIntrosToDurationList(durations);
        addLevelsToDurationList(durations);

        if (isDuringEmoScene(currentTime)) {
            return null;
        }

        for (Map.Entry<String, Duration> nameDurationPair : durations.entrySet()) {
            Integer lastIntroIndex = intros.length - 1;
            Duration lastIntroStartTime = translator.convertToDuration(intros[lastIntroIndex].getStart());
            Duration beginningOfIntroSlides = translator.convertToDuration(intros[0].getStart());
            isIntroVisited(currentTime, lastIntroStartTime, beginningOfIntroSlides);
            Duration duration = nameDurationPair.getValue();

            if (currentTime.lessThan(duration)) {
                if (duration.equals(beginningOfIntroSlides) && visitedIntro) {
                    return translator.convertToDuration(credits[0].getStart());
                }
                return duration;
            }
        }
        return null;
    }

    private void addLevelsToDurationList(HashMap<String, Duration> durations) {
        for (Level level : levels) {
            String branchLevelPrefix = level.getBranch() + "-" + level.getLevel();
            String levelStartLabel = branchLevelPrefix + "-start";
            String levelEndLabel = branchLevelPrefix + "-end";
            durations.put(levelStartLabel, translator.convertToDuration(level.getStart()));
            durations.put(levelEndLabel, translator.convertToDuration(level.getStart()));
        }
    }

    private void addIntrosToDurationList(HashMap<String, Duration> durations) {
        for (Intro intro : intros) {
            String introStartLabel = "intro-" + intro.getIntro() + "-start";
            String introEndLabel = "intro-" + intro.getIntro() + "-end";
            durations.put(introStartLabel, translator.convertToDuration(intro.getStart()));
            durations.put(introEndLabel, translator.convertToDuration(intro.getEnd()));
        }
    }

    private void isIntroVisited(Duration currentTime, Duration thirdIntroStart, Duration beginning) {
        if (currentTime.greaterThan(thirdIntroStart) || currentTime.lessThan(beginning)) {
            visitedIntro = true;
        }
    }

    private boolean isDuringEmoScene(Duration currentTime) {
        ArrayList<ArrayList<Duration>> emoTimes = new ArrayList<>();

        addEmoTimesToArray(emoTimes);

        addLastLevelEmoToIntroTime(emoTimes);

        for (ArrayList<Duration> times : emoTimes) {
            if (currentTime.greaterThan(times.get(0)) && currentTime.lessThan(times.get(1))) {
                return true;
            }
        }

        return false;
    }

    private void addLastLevelEmoToIntroTime(ArrayList<ArrayList<Duration>> emoTimes) {
        /*
        NOTE: because the Intro slides are directly after the end of the non-emotion measuring part of the last level,
        the end time for this has to be the start of the first Intro slide instead of the first Credit slide
        */
        ArrayList<Duration> currTimes = new ArrayList<>();
        currTimes.add(translator.convertToDuration(levels[levels.length - 1].getEnd()));
        currTimes.add(translator.convertToDuration(intros[0].getStart()));
        emoTimes.add(currTimes);
    }

    private void addEmoTimesToArray(ArrayList<ArrayList<Duration>> emoTimes) {
        for (int i = 0; i < levels.length - 1; i++) {
            ArrayList<Duration> currTimes = new ArrayList<>();
            currTimes.add(translator.convertToDuration(levels[i].getEnd()));
            currTimes.add(translator.convertToDuration(levels[i + 1].getStart()));
            emoTimes.add(currTimes);
        }
    }
}
